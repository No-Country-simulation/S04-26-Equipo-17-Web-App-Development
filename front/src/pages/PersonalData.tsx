import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import { Input } from "@/components/ui/input";

const TAX_REGIMES = ["Simple", "Común", "No declarante"] as const;
type TaxRegime = (typeof TAX_REGIMES)[number];

export default function PersonalData() {
  const navigate = useNavigate();
  const [taxRegime, setTaxRegime] = useState<TaxRegime>("Simple");
  const [chargesIva, setChargesIva] = useState<boolean>(false);

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
                {/* Heading */}
                <div className="flex items-start gap-4">
                  <div className="mt-0.5 flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#f6dbd1] text-[#c97158]">
                    <User className="h-5 w-5" />
                  </div>
                  <div>
                    <h1 className="text-[2.5rem] leading-tight font-bold tracking-tight text-[#1b2234]">
                      ¡Empecemos por lo básico! 👋
                    </h1>
                    <p className="mt-1 max-w-[66ch] text-[1.15rem] leading-relaxed text-[#3f4760]">
                      Los usamos para emitir tu contrato y configurar el cumplimiento fiscal en
                      Colombia.
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
                        placeholder="Sofía"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Apellidos">
                      <Input
                        placeholder="Restrepo Quintero"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Cómo te llamamos" hint="Tu preferido">
                      <Input
                        placeholder="Sofi"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Fecha de nacimiento" hint="DD/MM/AAAA">
                      <Input
                        placeholder="14 / 03 / 1992"
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
                        <span className="text-base">🇨🇴</span>
                        <select className="flex-1 bg-transparent text-base text-[#1b2234] outline-none">
                          <option value="CO">Colombia</option>
                          <option value="MX">México</option>
                          <option value="AR">Argentina</option>
                          <option value="PE">Perú</option>
                        </select>
                      </div>
                    </FieldGroup>

                    <FieldGroup label="Cédula/CC" hint="10 dígitos">
                      <Input
                        placeholder="1.014.789.332"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base font-mono"
                      />
                    </FieldGroup>
                  </div>

                  <div className="mt-4 grid gap-4 sm:grid-cols-2">
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

                    <FieldGroup label="¿Cobra IVA?">
                      <div className="flex gap-2">
                        {(["Sí", "No"] as const).map((option) => {
                          const isYes = option === "Sí";
                          const active = isYes ? chargesIva : !chargesIva;
                          return (
                            <button
                              key={option}
                              type="button"
                              onClick={() => setChargesIva(isYes)}
                              className={[
                                "flex-1 rounded-[10px] py-2.5 text-sm font-semibold transition-colors",
                                active
                                  ? "bg-[#182237] text-white"
                                  : "border border-[#e0d8cc] bg-white text-[#3f4760] hover:border-[#b8b0a5]",
                              ].join(" ")}
                            >
                              {option}
                            </button>
                          );
                        })}
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
                    <FieldGroup label="Teléfono">
                      <Input
                        type="tel"
                        placeholder="+57 300 123 4567"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>

                    <FieldGroup label="Correo">
                      <Input
                        type="email"
                        placeholder="sofia@ejemplo.com"
                        className="h-11 rounded-[12px] border-[#e0d8cc] bg-white px-4 text-base"
                      />
                    </FieldGroup>
                  </div>
                </div>
              </article>
            </section>

            <ProcessPaginationFooter
              className="rounded-none border-0 border-t border-[#ddd4c8] px-6"
              onBack={() => navigate(-1)}
              onNext={() => navigate("/documents")}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName="Sofia Restrepo"
              country="Colombia"
              currency="COP"
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
