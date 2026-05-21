export interface  InvitationResponse {
    onboardingId: number;
    token: string;
    expiresAt: string;
    invitationLink: string;
    monthlyFee: string;
    contractDuration: string;
    currency: string;
    company: string;

}