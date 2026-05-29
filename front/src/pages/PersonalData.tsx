import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import { useApp } from "@/context/appContext";
import { Input } from "@/components/ui/input";
import { ApiError } from "@/lib/api";
import { useStep1 } from "@/lib/queries";

const TAX_REGIMES = ["Simple", "Común", "No declarante"] as const;
type TaxRegime = (typeof TAX_REGIMES)[number];

const COUNTRIES = [
  { code: "CO", label: "Colombia", flag: "🇨🇴" },
  { code: "MX", label: "México", flag: "🇲🇽" },
  { code: "AR", label: "Argentina", flag: "🇦🇷" },
  { code: "PE", label: "Perú", flag: "🇵🇪" },
  { code: "ES", label: "España", flag: "🇪🇸" },
] as const;

type CountryCode = (typeof COUNTRIES)[number]["code"];

export default function PersonalData() {
  const navigate = useNavigate();
  const { savedAt, clearSession } = useApp();
  const step1 = useStep1();

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [preferredName, setPreferredName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [country, setCountry] = useState<CountryCode>("CO");
  const [idDocumentNumber, setIdDocumentNumber] = useState("");
  const [taxRegime, setTaxRegime] = useState<TaxRegime>("Simple");
  const [phone, setPhone] = useState("");
  const [submitError, setSubmitError] = useState<string | null>(null);

  const selectedCountry = COUNTRIES.find((c) => c.code === country)!;

  const handleNext = () => {
    setSubmitError(null);
    step1.mutate(
      {
        firstName,
        lastName,
        preferredName: preferredName || undefined,
        birthDate,
        countryIso: country,
        idDocumentNumber,
        taxRegime,
        phone,
      },
      {
        onSuccess: () => navigate("/documents"),
        onError: (err) => {
          if (err instanceof ApiError && err.status === 401) {
            clearSession();
            navigate("/");
            return;
          }
          setSubmitError(err instanceof Error ? err.message : "Error al guardar los datos");
        },
      },
    );
  };

  const isReady = !!firstName && !!lastName && !!birthDate && !!idDocumentNumber && !!phone;

  return (
    <main className="min-h-dvh bg-[#efe9e1]">
      <section className="min-h-dvh border border-[#ddd4c8] bg-[#f7f3ed]">
        <NorthpayHeader className="rounded-none border-0 border-b border-[#ddd4c8]" />

        <div className="grid min-h-[calc(100dvh-74px)] xl:grid-cols-[1fr_290px]">
          <div className="flex min-h-0 flex-col">
            <ProcessStatusBar
              currentStep={1}
              totalSteps={5}
              minutesRemaining="~ 6 min"
              className="px-6 pt-6"
            />

            <section className="relative mt-4 flex-1 overflow-y-auto px-6 pb-6">
              <article>
                <div className="flex items-start gap-4">
                  <div className="mt-0.5 flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#f6dbd1] text-[#c97158]">
                    <User className="h-5 w-5" />
                  </div>
                  <div>
                    <h1 className="text-[2.5rem] leading-tight font-bold tracking-tight text-[#1b2234]">
                      ¡Empecemos por lo básico! 👋
                    </h1>
                    <p className="mt-1 max-w-[66ch] text-[1.15rem] leading-relaxed text-[#3f4760]">
                      Los usamos para emitir tu contrato y configurar el cumplimiento fiscal en{" "}
                      {selectedCountry.label}.
                    </p>
                  </div>
                </div>

                {/* TU IDENTIDAD */}
                <div className="mt-8">
                  <p className="mb-4 border-l-2 border-[#ff7a59] pl-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    TU IDENTIDAD
                  </p>
                  <div className="grid gap-4 sm:grid-cols-2">
                    <FieldGroup label="Nombre legal">
                      <Input
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        placeholder="Tu nombre legal"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Apellidos">
                      <Input
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        placeholder="Tus apellidos"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Cómo te llamamos" hint="Opcional">
                      <Input
                        value={preferredName}
                        onChange={(e) => setPreferredName(e.target.value)}
                        placeholder="¿Cómo te gusta que te llamen?"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Fecha de nacimiento">
                      <Input
                        type="date"
                        value={birthDate}
                        onChange={(e) => setBirthDate(e.target.value)}
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base font-mono"
                      />
                    </FieldGroup>
                  </div>
                </div>

                {/* PAÍS E IMPUESTOS */}
                <div className="mt-8">
                  <p className="mb-4 border-l-2 border-[#ff7a59] pl-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    PAÍS E IMPUESTOS
                  </p>

                  <div className="grid gap-4 sm:grid-cols-2">
                    <FieldGroup label="País de residencia">
                      <div className="flex h-11 w-full items-center gap-2 rounded-[12px] border border-[#e0d8cc] bg-white px-4">
                        <span className="text-base">{selectedCountry.flag}</span>
                        <select
                          value={country}
                          onChange={(e) => setCountry(e.target.value as CountryCode)}
                          className="flex-1 bg-transparent text-base text-[#1b2234] outline-none"
                        >
                          {COUNTRIES.map((c) => (
                            <option key={c.code} value={c.code}>
                              {c.label}
                            </option>
                          ))}
                        </select>
                      </div>
                    </FieldGroup>

                    <FieldGroup label="Documento de identificación" hint="Solo dígitos">
                      <Input
                        value={idDocumentNumber}
                        onChange={(e) => setIdDocumentNumber(e.target.value.replace(/\D/g, ""))}
                        placeholder="0000000000"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base font-mono"
                      />
                    </FieldGroup>
                  </div>

                  <div className="mt-4">
                    <FieldGroup label="Régimen tributario">
                      <div className="flex gap-2">
                        {TAX_REGIMES.map((regime) => (
                          <button
                            key={regime}
                            type="button"
                            onClick={() => setTaxRegime(regime)}
                            className={[
                              "flex-1 rounded-[10px] py-2.5 text-sm font-semibold transition-colors",
                              taxRegime === regime
                                ? "bg-[#182237] text-white"
                                : "border border-[#e0d8cc] bg-white text-[#3f4760] hover:border-[#b8b0a5]",
                            ].join(" ")}
                          >
                            {regime}
                          </button>
                        ))}
                      </div>
                    </FieldGroup>
                  </div>
                </div>

                {/* CONTACTO */}
                <div className="mt-8">
                  <p className="mb-4 border-l-2 border-[#ff7a59] pl-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    CONTACTO
                  </p>
                  <div className="grid gap-4 sm:grid-cols-2">
                    <FieldGroup label="Teléfono" hint="Formato +57 300 000 0000">
                      <Input
                        type="tel"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        placeholder="+57 300 000 0000"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>
                  </div>
                </div>

                {submitError && (
                  <div className="mt-4 rounded-2xl bg-[#fff5f3] px-4 py-3 text-sm text-[#d04f35]">
                    <span className="font-semibold">Error: </span>
                    {submitError}
                  </div>
                )}
              </article>
            </section>

            <ProcessPaginationFooter
              className="rounded-none border-0 border-t border-[#ddd4c8] px-6"
              savedAt={savedAt}
              onBack={() => navigate(-1)}
              onNext={handleNext}
              nextDisabled={!isReady || step1.isPending}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName="Sofia Restrepo"
              country={selectedCountry.label}
              countryCode={selectedCountry.code}
              currency={selectedCountry.code}
              steps={[
                { number: 1, label: "Datos personales", active: true },
                { number: 2, label: "Documentos" },
                { number: 3, label: "Firma de contrato" },
                { number: 4, label: "Método de pago" },
                { number: 5, label: "Verificación de identidad" },
              ]}
            />
          </aside>
        </div>
      </section>
    </main>
  );
}

function FieldGroup({
  label,
  hint,
  children,
}: {
  label: string;
  hint?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="flex flex-col gap-1.5">
      <div className="flex items-baseline justify-between gap-2">
        <label className="text-sm font-semibold text-[#1b2234]">{label}</label>
        {hint && <span className="text-xs text-[#7a8094]">{hint}</span>}
      </div>
      {children}
    </div>
  );
}
