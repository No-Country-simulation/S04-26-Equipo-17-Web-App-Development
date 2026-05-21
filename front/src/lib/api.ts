const BASE_URL = "https://n4nd0-northpay-backend.hf.space";

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
    this.name = "ApiError";
  }
}

interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

async function request<T>(
  path: string,
  {
    token,
    body,
    method = "GET",
  }: { token?: string; body?: unknown; method?: string } = {}
): Promise<T> {
  const headers: Record<string, string> = {};

  if (token) headers["Authorization"] = `Bearer ${token}`;
  if (body) headers["Content-Type"] = "application/json";

  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const errorBody = await res.json().catch(() => null);
    throw new ApiError(res.status, errorBody?.message ?? `Error ${res.status}`);
  }

  const text = await res.text();
  if (!text) return null as T;

  const json = JSON.parse(text);
  // Unwrap { success, data } envelope if present
  return "data" in json ? (json as ApiResponse<T>).data : (json as T);
}

async function requestForm<T>(path: string, formData: FormData, token: string): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  });

  if (!res.ok) {
    const errorBody = await res.json().catch(() => null);
    throw new ApiError(res.status, errorBody?.message ?? `Error ${res.status}`);
  }

  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (null as T);
}

export const api = {
  get: <T>(path: string, token?: string) => request<T>(path, { token }),
  post: <T>(path: string, body: unknown, token: string) =>
    request<T>(path, { method: "POST", body, token }),
  put: <T>(path: string, body: unknown, token: string) =>
    request<T>(path, { method: "PUT", body, token }),
  postForm: <T>(path: string, formData: FormData, token: string) =>
    requestForm<T>(path, formData, token),
};
