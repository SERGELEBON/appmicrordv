// Configuration du client API
const API_CONFIG = {
  AUTH_URL: import.meta.env.VITE_API_AUTH_URL || 'http://localhost:8081/api',
  RDV_URL: import.meta.env.VITE_API_RDV_URL || 'http://localhost:8083/api',
  GATEWAY_URL: import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8080/api',
  CHAT_URL: import.meta.env.VITE_API_CHAT_URL || 'http://localhost:8083/api',
  TIMEOUT: parseInt(import.meta.env.VITE_API_TIMEOUT || '30000'), // 30 secondes pour inscription/email
};

export class ApiClient {
  private baseURL: string;
  private timeout: number;

  constructor(baseURL: string, timeout: number = API_CONFIG.TIMEOUT) {
    this.baseURL = baseURL;
    this.timeout = timeout;
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseURL}${endpoint}`;
    
    const defaultHeaders = {
      'Content-Type': 'application/json',
    };

    // Ajouter le token d'authentification si disponible
    const token = localStorage.getItem('authToken');
    if (token) {
      defaultHeaders['Authorization'] = `Bearer ${token}`;
    }

    const config: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    };

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), this.timeout);

      const response = await fetch(url, {
        ...config,
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      return data;
    } catch (error) {
      if (error.name === 'AbortError') {
        throw new Error('Request timeout');
      }
      throw error;
    }
  }

  async get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'GET' });
  }

  async post<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async put<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }
}

// Instances de clients API
export const authApi = new ApiClient(API_CONFIG.AUTH_URL);
export const rdvApi = new ApiClient(API_CONFIG.RDV_URL);
export const chatApi = new ApiClient(API_CONFIG.CHAT_URL);

export default API_CONFIG;