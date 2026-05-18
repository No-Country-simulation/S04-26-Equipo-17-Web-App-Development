import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import {
  BadgeCheck,
  FileText,
  Home,
  Landmark,
  Upload,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function Documents() {
  const navigate = useNavigate();

  return (
    <main className="min-h-dvh bg-[#efe9e1]">
      <section className="min-h-dvh border border-[#ddd4c8] bg-[#f7f3ed]">
        <NorthpayHeader className="rounded-none border-0 border-b border-[#ddd4c8]" />

        <div className="grid min-h-[calc(100dvh-74px)] xl:grid-cols-[1fr_290px]">
          <div className="flex min-h-0 flex-col">
            <ProcessStatusBar
              currentStep={2}
              totalSteps={5}
              minutesRemaining="~ 4 min"
              className="px-6 pt-6"
            />

            <section className="relative mt-4 flex-1 overflow-y-auto px-6 pb-6">
              <article>
                <div className="flex items-start gap-4">
                  <div className="mt-0.5 flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#f6dbd1] text-[#9f95b5]">
                    <FileText className="h-5 w-5" />
                  </div>
                  <div>
                    <h1 className="text-[2.5rem] leading-tight font-bold tracking-tight text-[#1b2234]">
                      Sube tus documentos
                    </h1>
                    <p className="mt-1 max-w-[66ch] text-[1.15rem] leading-relaxed text-[#3f4760]">
                      Los usamos solo para verificar tu identidad y tu derecho a facturar desde Colombia.
                    </p>
                  </div>
                </div>

                <div className="mt-6 rounded-3xl bg-[linear-gradient(90deg,#dbe4f4_0%,#f6d9ce_100%)] p-5">
                  <div className="flex flex-wrap items-center justify-between gap-4">
                    <div className="flex items-center gap-4">
                      <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-[#f5f8ff] text-[#2d67d8] shadow-[0_8px_20px_rgba(45,103,216,0.15)]">
                        <Upload className="h-7 w-7" />
                      </div>
                      <div>
                        <p className="text-[2rem] leading-none font-semibold text-[#1f2740]">
                          Arrastra tus archivos aqui
                        </p>
                        <p className="mt-2 text-sm text-[#4f5872]">
                          PDF, JPG o PNG · hasta 12 MB · Verificacion automatica en menos de 30 segundos ✨
                        </p>
                      </div>
                    </div>

                    <button
                      type="button"
                      className="h-12 rounded-[14px] bg-[#182237] px-8 text-base font-semibold text-white"
                    >
                      Buscar archivos
                    </button>
                  </div>
                </div>

                <div className="mt-6 flex items-center justify-between gap-3">
                  <p className="text-sm font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    Requisitos para Colombia
                  </p>
                  <p className="text-sm font-semibold text-[#20af73]">2 de 3 listos 🎉</p>
                </div>

                <div className="mt-3 space-y-3">
                  <div className="rounded-2xl border border-[#e0d8cc] bg-white px-4 py-3">
                    <div className="flex items-center justify-between gap-3">
                      <div className="flex min-w-0 items-center gap-3">
                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#bfead8] text-[#1f8d66]">
                          <BadgeCheck className="h-5 w-5" />
                        </div>
                        <div className="min-w-0">
                          <p className="truncate text-[1.4rem] leading-none font-semibold text-[#1f2740]">
                            Cédula_Sofia.pdf
                          </p>
                          <p className="mt-1 truncate text-sm text-[#505a74]">
                            Identificacion oficial · CC 1014789332 · Coincidencia ✓
                          </p>
                        </div>
                      </div>

                      <span className="inline-flex items-center rounded-full bg-[#d8f4e8] px-3 py-1 text-xs font-semibold text-[#1ea56d]">
                        ✓ Verificado
                      </span>
                    </div>
                  </div>

                  <div className="rounded-2xl border border-[#e0d8cc] bg-white px-4 py-3">
                    <div className="flex items-center justify-between gap-3">
                      <div className="flex min-w-0 items-center gap-3">
                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#d9e5f7] text-[#5d6d91]">
                          <Home className="h-5 w-5" />
                        </div>
                        <div className="min-w-0">
                          <p className="truncate text-[1.4rem] leading-none font-semibold text-[#1f2740]">
                            Servicios_marzo.pdf
                          </p>
                          <p className="mt-1 truncate text-sm text-[#505a74]">
                            Comprobante de domicilio · Leyendo direccion y fecha...
                          </p>
                        </div>
                      </div>

                      <span className="inline-flex items-center rounded-full bg-[#dce8ff] px-3 py-1 text-xs font-semibold text-[#2d67d8]">
                        ● Escaneando
                      </span>
                    </div>
                  </div>

                  <div className="rounded-2xl border border-dashed border-[#cfc5b6] bg-[#fbf9f4] px-4 py-3">
                    <div className="flex items-center justify-between gap-3">
                      <div className="flex min-w-0 items-center gap-3">
                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#ebe7dd] text-[#7f8597]">
                          <Landmark className="h-5 w-5" />
                        </div>
                        <div className="min-w-0">
                          <p className="truncate text-[1.4rem] leading-none font-semibold text-[#6a728a]">
                            Extracto bancario
                          </p>
                          <p className="mt-1 truncate text-sm text-[#6a728a]">
                            Opcional · acelera la configuracion de pago
                          </p>
                        </div>
                      </div>

                      <div className="flex items-center gap-2">
                        <span className="inline-flex rounded-full bg-[#ece7db] px-3 py-1 text-xs font-semibold text-[#7d8397]">
                          Pendiente
                        </span>
                        <button
                          type="button"
                          className="h-9 rounded-xl border border-[#ddd3c5] bg-white px-4 text-sm font-semibold text-[#1f2740]"
                        >
                          Subir
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="mt-4 rounded-2xl bg-[#f2e6c5] px-4 py-3 text-sm text-[#3f4760]">
                  <span className="font-semibold">💡 Consejo:</span> los PDFs funcionan mejor. Si usas el celular,
                  asegurate de que se vean las 4 esquinas del documento.
                </div>
              </article>
            </section>

            <ProcessPaginationFooter
              className="rounded-none border-0 border-t border-[#ddd4c8] px-6"
              onBack={() => navigate("/")}
              onNext={() => navigate("/identityVerify")}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName="Sofia Restrepo"
              country="Colombia"
              currency="COP"
              steps={[
                { number: 1, label: "Datos personales", done: true },
                { number: 2, label: "Documentos", active: true },
                { number: 3, label: "Firma de contrato" },
                { number: 4, label: "Metodo de pago" },
                { number: 5, label: "Verificacion de identidad" },
              ]}
            />
          </aside>
        </div>
      </section>
    </main>
  );
}