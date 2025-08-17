import 'package:retrofit/retrofit.dart';
import 'package:dio/dio.dart';
import '../models/appointment.dart';
import '../constants/app_constants.dart';

part 'appointment_service.g.dart';

@RestApi(baseUrl: AppConstants.baseUrl)
abstract class AppointmentService {
  factory AppointmentService(Dio dio, {String baseUrl}) = _AppointmentService;

  @GET('/rdv/appointments')
  Future<List<Appointment>> getAppointments();

  @GET('/rdv/appointments/{id}')
  Future<Appointment> getAppointment(@Path('id') int id);

  @POST('/rdv/appointments')
  Future<Appointment> createAppointment(@Body() AppointmentRequest request);

  @PUT('/rdv/appointments/{id}')
  Future<Appointment> updateAppointment(
    @Path('id') int id, 
    @Body() Map<String, dynamic> updates
  );

  @DELETE('/rdv/appointments/{id}')
  Future<void> cancelAppointment(@Path('id') int id);

  @GET('/rdv/doctors')
  Future<List<Doctor>> getDoctors();

  @GET('/rdv/doctors/{id}')
  Future<Doctor> getDoctor(@Path('id') int id);

  @GET('/rdv/doctors/{id}/availability')
  Future<List<AppointmentSlot>> getDoctorAvailability(
    @Path('id') int doctorId,
    @Query('date') String date,
  );

  @GET('/rdv/doctors/specialty/{specialty}')
  Future<List<Doctor>> getDoctorsBySpecialty(@Path('specialty') String specialty);

  @GET('/rdv/appointments/patient/{patientId}')
  Future<List<Appointment>> getPatientAppointments(@Path('patientId') int patientId);

  @GET('/rdv/appointments/doctor/{doctorId}')
  Future<List<Appointment>> getDoctorAppointments(@Path('doctorId') int doctorId);

  @POST('/rdv/appointments/{id}/confirm')
  Future<void> confirmAppointment(@Path('id') int id);

  @POST('/rdv/appointments/{id}/reschedule')
  Future<Appointment> rescheduleAppointment(
    @Path('id') int id,
    @Body() Map<String, String> newDateTime,
  );
}