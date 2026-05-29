import { useMutation, useQuery } from "@tanstack/react-query";

import { useApp } from "@/context/appContext";

import { api } from "./api";
import type {
  CorrectionComment,
  Document,
  DocumentsResponse,
  InvitationResponse,
  Step1Response,
  StepResponse,
} from "./types";

// ─── Invitation ────────────────────────────────────────────────────────────

export function useInvitation(token: string) {
  return useQuery({
    queryKey: ["invitation", token],
    queryFn: () => api.get<InvitationResponse>(`/api/invitations/${token}`),
    enabled: token.length > 0,
    retry: 0,
  });
}

// ─── Step 1 — Personal data ────────────────────────────────────────────────

interface Step1Body {
  firstName: string;
  lastName: string;
  preferredName?: string;
  birthDate: string; // yyyy-MM-dd
  countryIso: string;
  idDocumentNumber: string;
  taxRegime: string;
  phone: string;
}

export function useStep1() {
  const { session, updateStep, updateFullName, updateSavedAt } = useApp();

  return useMutation({
    mutationFn: (body: Step1Body) => {
      if (!session) throw new Error("No active session");
      return api.put<Step1Response>(
        `/api/onboarding/${session.onboardingId}/step1`,
        body,
        session.sessionToken
      );
    },
    onSuccess: (data) => {
      updateStep(data.currentStep);
      updateFullName(`${data.firstName} ${data.lastName}`);
      updateSavedAt(new Date(data.updatedAt));
    },
  });
}

// ─── Step 2 — Documents ────────────────────────────────────────────────────

export function useUploadDocuments() {
  const { session, updateStep, updateSavedAt } = useApp();

  return useMutation({
    mutationFn: ({ files, types }: { files: File[]; types: string[] }) => {
      if (!session) throw new Error("No active session");

      const formData = new FormData();
      files.forEach((file) => formData.append("files", file));

      const params = new URLSearchParams();
      types.forEach((t) => params.append("types", t));

      return api.postForm<DocumentsResponse>(
        `/api/onboarding/${session.onboardingId}/documents?${params.toString()}`,
        formData,
        session.sessionToken
      );
    },
    onSuccess: (data) => {
      updateStep(data.currentStep);
      // DocumentsResponse no tiene updatedAt — usamos la hora local de confirmación
      updateSavedAt(new Date());
    },
  });
}

// ─── Step 3 — Contract ─────────────────────────────────────────────────────

export function useContractPreviewUrl() {
  const { session } = useApp();
  if (!session) return null;
  return `https://n4nd0-northpay-backend.hf.space/api/onboarding/${session.onboardingId}/contract-preview`;
}

export function useSignContract() {
  const { session, updateStep, updateSavedAt } = useApp();

  return useMutation({
    mutationFn: () => {
      if (!session) throw new Error("No active session");
      return api.post<StepResponse>(
        `/api/onboarding/${session.onboardingId}/step3/contract`,
        {},
        session.sessionToken
      );
    },
    onSuccess: (data) => {
      updateStep(data.currentStep);
      updateSavedAt(new Date(data.updatedAt));
    },
  });
}

// ─── Step 4 — Payment ──────────────────────────────────────────────────────

interface PaymentBody {
  accountType: string;
  details: Record<string, unknown>;
}

export function useConfigurePayment() {
  const { session, updateStep, updateSavedAt } = useApp();

  return useMutation({
    mutationFn: (body: PaymentBody) => {
      if (!session) throw new Error("No active session");
      return api.post<StepResponse>(
        `/api/onboarding/${session.onboardingId}/step4/payment`,
        body,
        session.sessionToken
      );
    },
    onSuccess: (data) => {
      updateStep(data.currentStep);
      updateSavedAt(new Date(data.updatedAt));
    },
  });
}

// ─── Step 5 — Selfie ───────────────────────────────────────────────────────

export function useUploadSelfie() {
  const { session, updateStep, updateSavedAt } = useApp();

  return useMutation({
    mutationFn: (file: File) => {
      if (!session) throw new Error("No active session");

      const formData = new FormData();
      formData.append("file", file);

      return api.postForm<StepResponse>(
        `/api/onboarding/${session.onboardingId}/step5/selfie`,
        formData,
        session.sessionToken
      );
    },
    onSuccess: (data) => {
      updateStep(data.currentStep);
      updateSavedAt(new Date(data.updatedAt));
    },
  });
}

// ─── Documents list ────────────────────────────────────────────────────────

export function useDocuments(docType?: string) {
  const { session } = useApp();
  const path = docType
    ? `/api/onboarding/${session?.onboardingId}/documents?type=${docType}`
    : `/api/onboarding/${session?.onboardingId}/documents`;

  return useQuery({
    queryKey: ["documents", session?.onboardingId, docType],
    queryFn: () => api.get<Document[]>(path, session!.sessionToken),
    enabled: !!session,
  });
}

// ─── Corrections ───────────────────────────────────────────────────────────

export function useCorrections() {
  const { session } = useApp();

  return useQuery({
    queryKey: ["corrections", session?.onboardingId],
    queryFn: () =>
      api.get<CorrectionComment[]>(
        `/api/onboarding/${session!.onboardingId}/comments`,
        session!.sessionToken
      ),
    enabled: !!session,
  });
}
