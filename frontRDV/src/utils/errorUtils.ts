// Fonction utilitaire pour nettoyer et améliorer les messages d'erreur
export const cleanErrorMessage = (error: string): string => {
  // Messages d'erreur communs et leurs traductions user-friendly
  const errorMappings: Record<string, string> = {
    'Invalid username or password': 'Email ou mot de passe incorrect',
    'Username or email already exists': 'Cet email est déjà utilisé',
    'User registered successfully!': 'Compte créé avec succès !',
    'Bad credentials': 'Email ou mot de passe incorrect',
    'duplicate key value violates unique constraint': 'Cet utilisateur existe déjà',
    'refresh_tokens_user_id_key': 'Erreur de connexion, veuillez réessayer',
    'could not execute statement': 'Erreur de connexion, veuillez réessayer',
    'Failed to fetch': 'Impossible de se connecter au serveur',
    'Network Error': 'Erreur de réseau, vérifiez votre connexion',
    'Request timeout': 'Délai de connexion dépassé, veuillez réessayer',
    'Internal Server Error': 'Erreur du serveur, veuillez réessayer plus tard'
  };

  // Nettoyer le message d'erreur
  let cleanMessage = error;

  // Supprimer les parties techniques comme les stack traces SQL
  if (cleanMessage.includes('[ERROR:')) {
    const errorStart = cleanMessage.indexOf('[ERROR:');
    const errorEnd = cleanMessage.indexOf(']', errorStart);
    if (errorEnd > errorStart) {
      cleanMessage = cleanMessage.substring(0, errorStart).trim();
    }
  }

  // Supprimer les parties SQL
  if (cleanMessage.includes('SQL [')) {
    const sqlStart = cleanMessage.indexOf('SQL [');
    cleanMessage = cleanMessage.substring(0, sqlStart).trim();
  }

  // Supprimer les contraintes techniques
  if (cleanMessage.includes('constraint [')) {
    const constraintStart = cleanMessage.indexOf('constraint [');
    cleanMessage = cleanMessage.substring(0, constraintStart).trim();
  }

  // Chercher une correspondance exacte d'abord
  for (const [key, value] of Object.entries(errorMappings)) {
    if (cleanMessage.includes(key)) {
      return value;
    }
  }

  // Si aucune correspondance exacte, retourner un message générique amélioré
  if (cleanMessage.includes('HTTP 400')) {
    return 'Données invalides, veuillez vérifier vos informations';
  }
  
  if (cleanMessage.includes('HTTP 401')) {
    return 'Email ou mot de passe incorrect';
  }
  
  if (cleanMessage.includes('HTTP 403')) {
    return 'Accès non autorisé';
  }
  
  if (cleanMessage.includes('HTTP 404')) {
    return 'Service non trouvé';
  }
  
  if (cleanMessage.includes('HTTP 500')) {
    return 'Erreur du serveur, veuillez réessayer plus tard';
  }

  // Message par défaut si rien ne correspond
  return cleanMessage.length > 100 
    ? 'Une erreur est survenue, veuillez réessayer'
    : cleanMessage || 'Une erreur est survenue';
};