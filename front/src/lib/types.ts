export type OnboardingStatus =
  | "INVITED"
  | "IN_PROGRESS"
  | "DOCUMENTS_UPLOADED"
  | "CONTRACT_SIGNED"
  | "PAYMENT_CONFIGURED"
  | "PENDING_VERIFICATION"
  | "CORRECTION_REQUIRED"
  | "ACTIVATED"
  | "REJECTED";

export interface InvitationResponse {
  onboardingId: number;
  /** El backend devuelve este campo como "token", no "sessionToken" */
  token: string;
  expiresAt?: string;
  invitationLink?: string;
  timeRemaining?: string;
  company?: string;
  monthlyFee?: string;
  contractDuration?: string;
  currency?: string;
}

export interface InvitationDetails {
  onboardingId: number;
  token: string;
  expiresAt: string;
  invitationLink: string;
  monthlyFee: string;
  contractDuration: string;
  currency: string;
  company: string;
}

export interface Step1Response {
  onboardingId: number;
  status: OnboardingStatus;
  currentStep: number;
  fullName: string;
  countryIso: string;
  updatedAt: string;
}

export interface Document {
  id: number;
  docType: "IDENTITY" | "TAX_ID" | "PROOF_OF_ADDRESS" | "SIGNED_CONTRACT" | "SELFIE";
  fileUrl: string;
  status: "PENDING" | "SCANNING" | "VERIFIED" | "REJECTED";
  uploadedAt: string;
}

export interface DocumentsResponse {
  onboardingId: number;
  status: OnboardingStatus;
  currentStep: number;
  documents: Document[];
}

export interface StepResponse {
  id: number;
  status: OnboardingStatus;
  currentStep: number;
  updatedAt: string;
}

export interface CorrectionComment {
  observations: string;
  previousStatus: OnboardingStatus;
  newStatus: OnboardingStatus;
  createdAt: string;
}
