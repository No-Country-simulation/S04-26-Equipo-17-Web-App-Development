import { useEffect } from "react";

import {
  Banknote,
  Building2,
  CircleCheckBig,
  CircleUserRound,
  CreditCard,
  FileBadge2,
  FileText,
  Globe,
  HandCoins,
  IdCard,
  Wallet,
} from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { useApp } from "@/context/appContext";
import { useInvitation } from "@/lib/queries";
import type { InvitationResponse } from "@/lib/types";
import { daysUntilExpiry } from "@/lib/utils";

const REQUIREMENTS = [
  {
    id: "01",
    title: "Datos personales",
    subtitle: "Dirección, identificación tributaria, fecha de nacimiento",
    icon: CircleUserRound,
  },
  { id: "02", title: "Documentos", subtitle: "Cédula + comprobante de domicilio", icon: FileText },
  { id: "03", title: "Contrato", subtitle: "Lee y firma tu acuerdo", icon: FileBadge2 },
  { id: "04", title: "Pago", subtitle: "Banco local, Wise o USDC — tú eliges", icon: Banknote },
  { id: "05", title: "Identidad", subtitle: "Una selfie rápida (~30 seg)", icon: IdCard },
] as const;

function buildHighlights(data: InvitationResponse) {
  return [
    { title: data.monthlyFee ?? "USD 5.200", subtitle: "Tarifa mensual", icon: Building2 },
    { title: data.contractDuration ?? "12 meses", subtitle: "Duración", icon: CircleCheckBig },
    { title: "Net 5", subtitle: "Ciclo de pago", icon: HandCoins },
    { title: data.currency ?? "COP / USD", subtitle: "Eliges tu moneda", icon: Wallet },
  ];
}

export default function OnBoarding() {
  const { token = "" } = useParams<{ token: string }>();
  const navigate = useNavigate();
  const { setSession } = useApp();

  const { data, isLoading, isError, error } = useInvitation(token);

  useEffect(() => {
    if (!data) return;
    setSession({
      onboardingId: data.onboardingId,
      sessionToken: data.sessionToken,
      currentStep: data.currentStep,
      fullName: data.fullName,
      email: data.email,
    });
  }, [data, setSession]);

  if (isLoading) return <OnBoardingLoading />;
  if (isError) return <OnBoardingError message={error?.message} />;
  if (!data) return null;

  const firstName = data.fullName?.split(" ")[0] || data.email.split("@")[0];
  const company = data.company ?? "Lattice & Loop";
  const highlights = buildHighlights(data);

  return (
    <main className="min-h-dvh w-full overflow-y-auto">
      <section className="grid min-h-dvh w-full bg-[#f5efe8] lg:grid-cols-[1.04fr_1fr]">
        <article className="relative overflow-hidden p-6 sm:p-8 lg:flex lg:flex-col lg:justify-between lg:p-10">
          <div className="pointer-events-none absolute -top-16 -left-16 h-56 w-56 rounded-full bg-[#f3d8cb]/90" />
          <div className="pointer-events-none absolute -right-20 -bottom-20 h-56 w-56 rounded-full bg-[#f0dfc4]" />

          <div className="relative z-10 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="flex h-7 w-7 items-center justify-center rounded-full bg-[#2a64d8] text-xs font-bold text-white">
                N
              </div>
              <span className="text-[1.7rem] font-semibold tracking-tight text-[#1b2434]">
                Northpay
              </span>
            </div>

            <button
              type="button"
              className="inline-flex items-center gap-2 rounded-full bg-[#f9dacc]/80 px-3 py-1 text-[0.72rem] font-semibold tracking-wide text-[#ff7a59]"
            >
              <Globe className="h-3.5 w-3.5" />
              ES · EN · PT
            </button>
          </div>

          <div className="relative z-10">
            <div className="mt-10 rounded-full bg-[#f6d9ce] px-4 py-2 text-xs font-semibold text-[#f0705d]">
              ● Invitación · expira en {daysUntilExpiry(data.expiresAt)}
            </div>

            <div className="mt-7 space-y-4">
              <h1 className="max-w-[18ch] text-[2.5rem] leading-[0.95] font-bold tracking-tight text-[#182134] sm:text-[3.2rem]">
                ¡Hola, {firstName}! 👋
                <br />
                <span className="text-[#727b92]">{company} quiere pagarte sin enredos.</span>
              </h1>
              <p className="max-w-[44ch] text-sm leading-relaxed text-[#3e4a62] sm:text-lg">
                Te acompañamos en 5 pasos, en unos 10 minutos. Tus datos están cifrados y solo se
                usan para configurar tu pago.
              </p>
            </div>

            <div className="mt-8 grid grid-cols-1 gap-3 sm:grid-cols-2">
              {highlights.map(({ icon: Icon, title, subtitle }) => (
                <div
                  key={subtitle}
                  className="flex items-center gap-3 rounded-2xl border border-[#e6ddd2] bg-[#f7f3ed] px-4 py-3"
                >
                  <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[#e8ecf7] text-[#5f7fd9]">
                    <Icon className="h-4 w-4" />
                  </div>
                  <div>
                    <p className="text-base font-semibold text-[#1d2639]">{title}</p>
                    <p className="text-xs text-[#76809a]">{subtitle}</p>
                  </div>
                </div>
              ))}
            </div>

            <div className="mt-8 flex flex-wrap items-center gap-3">
              <Button
                type="button"
                onClick={() => navigate("/personalData")}
                className="h-11 rounded-[0.85rem] bg-[#182237] px-6 text-sm font-semibold text-white hover:bg-[#12192a]"
              >
                Empezar onboarding →
              </Button>

              <Button
                type="button"
                variant="ghost"
                className="h-11 rounded-[0.85rem] px-2 text-sm font-semibold text-[#1e2740] hover:bg-[#ede5dc]"
              >
                Lo hago más tarde
              </Button>
            </div>
          </div>

          <p className="relative z-10 mt-5 flex items-center gap-2 text-[0.68rem] text-[#7d859c] sm:text-xs lg:mt-6">
            <CreditCard className="h-3.5 w-3.5" />
            Al continuar aceptás nuestra Política de Privacidad y Términos.
          </p>
        </article>

        <aside className="relative flex flex-col overflow-hidden bg-[linear-gradient(160deg,#111a30_0%,#1a243d_100%)] p-6 text-white sm:p-8 lg:p-9">
          <h2 className="mt-6 text-sm font-semibold tracking-[0.18em] text-[#ff7d5d] uppercase">
            Qué necesitarás
          </h2>

          <div className="mt-6 space-y-3">
            {REQUIREMENTS.map(({ id, icon: Icon, title, subtitle }) => (
              <div
                key={id}
                className="flex items-center gap-3 rounded-2xl border border-white/5 bg-white/5 px-4 py-3.5 backdrop-blur"
              >
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white/8 text-[#9aa5c4]">
                  <Icon className="h-5 w-5" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="text-[1.02rem] leading-none font-semibold text-white">{title}</p>
                  <p className="mt-1 truncate text-sm text-[#95a0bc]">{subtitle}</p>
                </div>
                <span className="text-sm font-semibold text-[#7984a2]">{id}</span>
              </div>
            ))}
          </div>

          <div className="mt-10 border-t border-white/10 pt-5 sm:mt-12 lg:mt-auto">
            <div className="grid grid-cols-3 gap-4 text-left">
              <div>
                <p className="text-4xl leading-none font-bold tracking-tight">180+</p>
                <p className="mt-1 text-xs text-[#9da7c3]">Países</p>
              </div>
              <div>
                <p className="text-4xl leading-none font-bold tracking-tight">2,4 d</p>
                <p className="mt-1 text-xs text-[#9da7c3]">Activación media</p>
              </div>
              <div>
                <p className="text-4xl leading-none font-bold tracking-tight">98%</p>
                <p className="mt-1 text-xs text-[#9da7c3]">Aprobación al primer intento</p>
              </div>
            </div>
          </div>
        </aside>
      </section>
    </main>
  );
}

function OnBoardingLoading() {
  return (
    <main className="min-h-dvh w-full bg-[#f5efe8] p-8">
      <div className="flex items-center gap-3">
        <Skeleton className="h-7 w-7 rounded-full" />
        <Skeleton className="h-7 w-28" />
      </div>
      <div className="mt-12 space-y-4">
        <Skeleton className="h-6 w-40 rounded-full" />
        <Skeleton className="h-14 w-96" />
        <Skeleton className="h-14 w-80" />
        <Skeleton className="h-5 w-72" />
      </div>
    </main>
  );
}

function OnBoardingError({ message }: { message?: string }) {
  const isExpired = message?.toLowerCase().includes("expir");

  return (
    <main className="flex min-h-dvh flex-col items-center justify-center bg-[#f5efe8] p-8 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-full bg-[#ffe4dc]">
        <span className="text-2xl">⚠️</span>
      </div>
      <h1 className="mt-6 text-2xl font-bold text-[#182134]">
        {isExpired ? "Esta invitación expiró" : "Invitación no válida"}
      </h1>
      <p className="mt-3 max-w-sm text-sm text-[#5c6681]">
        {isExpired
          ? "El link que usaste ya venció. Pedile a tu empresa que te envíe uno nuevo."
          : "No encontramos esta invitación. Revisá que el link esté completo."}
      </p>
    </main>
  );
}
