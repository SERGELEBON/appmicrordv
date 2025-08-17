import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../core/providers/appointment_provider.dart';
import '../../../core/models/appointment.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/constants/app_constants.dart';
import '../../../core/utils/responsive_utils.dart';
import '../../../core/widgets/safe_scroll_view.dart';

class BookAppointmentScreen extends ConsumerStatefulWidget {
  const BookAppointmentScreen({super.key});

  @override
  ConsumerState<BookAppointmentScreen> createState() => _BookAppointmentScreenState();
}

class _BookAppointmentScreenState extends ConsumerState<BookAppointmentScreen> {
  String? _selectedSpecialty;
  Doctor? _selectedDoctor;
  DateTime? _selectedDate;
  AppointmentSlot? _selectedSlot;
  final _notesController = TextEditingController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(doctorsProvider.notifier).loadDoctors();
    });
  }

  @override
  void dispose() {
    _notesController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final doctorsState = ref.watch(doctorsProvider);
    final appointmentsState = ref.watch(appointmentsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Prendre un rendez-vous'),
      ),
      body: ResponsiveUtils.buildCenteredContent(
        context: context,
        child: SafeScrollView(
          padding: ResponsiveUtils.getResponsivePadding(context),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
            // Étape 1: Choisir la spécialité
            _buildSectionHeader('1. Choisir une spécialité'),
            SizedBox(height: AppTheme.spacingSm),
            DropdownButtonFormField<String>(
              value: _selectedSpecialty,
              decoration: const InputDecoration(
                hintText: 'Sélectionner une spécialité',
                prefixIcon: Icon(Icons.medical_services),
              ),
              items: AppConstants.medicalSpecialties
                  .map((specialty) => DropdownMenuItem(
                        value: specialty,
                        child: Text(specialty),
                      ))
                  .toList(),
              onChanged: (value) {
                setState(() {
                  _selectedSpecialty = value;
                  _selectedDoctor = null;
                  _selectedDate = null;
                  _selectedSlot = null;
                });
                if (value != null) {
                  ref.read(doctorsProvider.notifier).loadDoctorsBySpecialty(value);
                }
              },
            ),
            SizedBox(height: AppTheme.spacingLg),

            // Étape 2: Choisir le médecin
            _buildSectionHeader('2. Choisir un médecin'),
            SizedBox(height: AppTheme.spacingSm),
            if (_selectedSpecialty == null)
              _buildDisabledStep('Choisissez d\'abord une spécialité')
            else if (doctorsState.isLoading)
              const Center(child: CircularProgressIndicator())
            else if (doctorsState.doctors.isEmpty)
              _buildDisabledStep('Aucun médecin disponible pour cette spécialité')
            else
              _buildDoctorsList(doctorsState.doctors),
            const SizedBox(height: 24),

            // Étape 3: Choisir la date
            _buildSectionHeader('3. Choisir une date'),
            const SizedBox(height: 8),
            if (_selectedDoctor == null)
              _buildDisabledStep('Choisissez d\'abord un médecin')
            else
              _buildDateSelector(),
            const SizedBox(height: 24),

            // Étape 4: Choisir l'heure
            _buildSectionHeader('4. Choisir un créneau'),
            const SizedBox(height: 8),
            if (_selectedDate == null)
              _buildDisabledStep('Choisissez d\'abord une date')
            else if (doctorsState.isLoading)
              const Center(child: CircularProgressIndicator())
            else
              _buildTimeSlots(doctorsState.availableSlots),
            const SizedBox(height: 24),

            // Étape 5: Notes (optionnel)
            _buildSectionHeader('5. Notes (optionnel)'),
            const SizedBox(height: 8),
            TextFormField(
              controller: _notesController,
              maxLines: 3,
              decoration: const InputDecoration(
                hintText: 'Décrivez brièvement votre motif de consultation...',
              ),
            ),
            const SizedBox(height: 32),

            // Bouton de confirmation
            SizedBox(
              width: double.infinity,
              height: 48,
              child: ElevatedButton(
                onPressed: _canBookAppointment() && !appointmentsState.isLoading
                    ? _bookAppointment
                    : null,
                child: appointmentsState.isLoading
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        ),
                      )
                    : const Text('Confirmer le rendez-vous'),
              ),
            ),
          ],
        ),
      ),
      )
    );
  }

  Widget _buildSectionHeader(String title) {
    return Text(
      title,
      style: Theme.of(context).textTheme.titleMedium?.copyWith(
        fontWeight: FontWeight.w600,
      ),
    );
  }

  Widget _buildDisabledStep(String message) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: Text(
        message,
        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
          color: AppTheme.textSecondary,
        ),
        textAlign: TextAlign.center,
      ),
    );
  }

  Widget _buildDoctorsList(List<Doctor> doctors) {
    return SafeListView(
      children: doctors.map((doctor) {
        final isSelected = _selectedDoctor?.id == doctor.id;
        return Card(
          color: isSelected ? AppTheme.primaryColor.withOpacity(0.1) : null,
          margin: EdgeInsets.only(bottom: AppTheme.spacingSm),
          child: ListTile(
            contentPadding: EdgeInsets.all(AppTheme.spacingMd),
            leading: CircleAvatar(
              backgroundColor: AppTheme.primaryColor,
              radius: ResponsiveUtils.isDesktop(context) ? 24 : 20,
              child: Text(
                '${doctor.firstName[0]}${doctor.lastName[0]}',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: ResponsiveUtils.getFontSize(context, 16),
                ),
              ),
            ),
            title: SafeText(
              'Dr. ${doctor.firstName} ${doctor.lastName}',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
                fontSize: ResponsiveUtils.getFontSize(context, 16),
              ),
            ),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                if (doctor.specialty != null) 
                  SafeText(
                    doctor.specialty!,
                    style: TextStyle(
                      fontSize: ResponsiveUtils.getFontSize(context, 14),
                    ),
                  ),
                if (doctor.rating != null)
                  Row(
                    children: [
                      Icon(Icons.star, size: 16, color: Colors.amber[600]),
                      SizedBox(width: AppTheme.spacingXs),
                      Text('${doctor.rating}/5'),
                    ],
                  ),
              ],
            ),
            trailing: isSelected
                ? Icon(Icons.check_circle, 
                    color: AppTheme.primaryColor,
                    size: ResponsiveUtils.getIconSize(context, 24))
                : null,
            onTap: () {
              setState(() {
                _selectedDoctor = doctor;
                _selectedDate = null;
                _selectedSlot = null;
              });
            },
          ),
        );
      }).toList(),
    );
  }

  Widget _buildDateSelector() {
    return Card(
      child: ListTile(
        leading: const Icon(Icons.calendar_today),
        title: Text(_selectedDate == null
            ? 'Sélectionner une date'
            : DateFormat('dd/MM/yyyy').format(_selectedDate!)),
        trailing: const Icon(Icons.arrow_forward_ios),
        onTap: () async {
          final date = await showDatePicker(
            context: context,
            initialDate: DateTime.now().add(const Duration(days: 1)),
            firstDate: DateTime.now().add(const Duration(days: 1)),
            lastDate: DateTime.now().add(const Duration(days: 30)),
          );
          
          if (date != null) {
            setState(() {
              _selectedDate = date;
              _selectedSlot = null;
            });
            
            ref.read(doctorsProvider.notifier).loadDoctorAvailability(
              _selectedDoctor!.id,
              date,
            );
          }
        },
      ),
    );
  }

  Widget _buildTimeSlots(List<AppointmentSlot> slots) {
    final availableSlots = slots.where((slot) => slot.isAvailable).toList();
    
    if (availableSlots.isEmpty) {
      return _buildDisabledStep('Aucun créneau disponible pour cette date');
    }

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: availableSlots.map((slot) {
        final isSelected = _selectedSlot?.dateTime == slot.dateTime;
        return FilterChip(
          selected: isSelected,
          label: Text(DateFormat('HH:mm').format(slot.dateTime)),
          onSelected: (selected) {
            setState(() {
              _selectedSlot = selected ? slot : null;
            });
          },
        );
      }).toList(),
    );
  }

  bool _canBookAppointment() {
    return _selectedSpecialty != null &&
        _selectedDoctor != null &&
        _selectedDate != null &&
        _selectedSlot != null;
  }

  Future<void> _bookAppointment() async {
    if (!_canBookAppointment()) return;

    final request = AppointmentRequest(
      doctorId: _selectedDoctor!.id,
      dateTime: _selectedSlot!.dateTime,
      notes: _notesController.text.trim().isEmpty ? null : _notesController.text.trim(),
    );

    final success = await ref.read(appointmentsProvider.notifier).createAppointment(request);

    if (success && mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Rendez-vous pris avec succès !'),
          backgroundColor: AppTheme.successColor,
        ),
      );
    }
  }
}