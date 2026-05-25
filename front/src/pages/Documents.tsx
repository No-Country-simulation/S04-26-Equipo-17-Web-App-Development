import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { BadgeCheck, FileText, Home, Landmark, Loader2 } from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import { useApp } from "@/context/appContext";
import { useUploadDocuments } from "@/lib/queries";

type DocType = "IDENTITY" | "PROOF_OF_ADDRESS" | "TAX_ID";

const DOC_REQUIREMENTS: Array<{
  type: DocType;
  label: string;
  description: string;
  required: boolean;
  Icon: React.ComponentType<{ className?: string }>;
}> = [
  {
    type: "IDENTITY",
    label: "Cédula / Pasaporte",
    description: "Identificación oficial vigente",
    required: true,
    Icon: BadgeCheck,
  },
  {
    type: "PROOF_OF_ADDRESS",
    label: "Comprobante de domicilio",
    description: "Factura de servicios del último mes",
    required: true,
    Icon: Home,
  },
  {
    type: "TAX_ID",
    label: "RUT / Documento tributario",
    description: "Opcional · acelera la configuración de pago",
    required: false,
    Icon: Landmark,
  },
];

function getInitials(name: string) {
  return name
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0] ?? "")
    .join("")
    .toUpperCase();
}

export default function Documents() {
  const navigate = useNavigate();
  const { session, savedAt } = useApp();
  const uploadMutation = useUploadDocuments();

  // Archivos seleccionados localmente — aún no subidos
  const [staged, setStaged] = useState<Partial<Record<DocType, File>>>({});
  const [uploadError, setUploadError] = useState<string | null>(null);
  const fileInputRefs = useRef<Partial<Record<DocType, HTMLInputElement | null>>>({});

  const handleInputChange =
    (type: DocType) => (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) setStaged((prev) => ({ ...prev, [type]: file }));
      e.target.value = "";
    };

  const handleNext = () => {
    setUploadError(null);
    const files: File[] = [];
    const types: string[] = [];

    DOC_REQUIREMENTS.forEach(({ type }) => {
      const file = staged[type];
      if (file) {
        files.push(file);
        types.push(type);
      }
    });

    uploadMutation.mutate(
      { files, types },
      {
        onSuccess: () => navigate("/sign"),
        onError: (err) => {
          const message = err instanceof Error ? err.message : "Error al subir los documentos";
          setUploadError(message);
        },
      },
    );
  };

  const requiredReady = DOC_REQUIREMENTS.filter((r) => r.required).every((r) => !!staged[r.type]);
  const stagedCount = DOC_REQUIREMENTS.filter((r) => !!staged[r.type]).length;
  const isUploading = uploadMutation.isPending;

  const fullName = session?.fullName ?? "Usuario";
  const initials = getInitials(fullName);
  const displayName = fullName.split(" ").slice(0, 2).join(" ");

  return (
    <main className="min-h-dvh bg-[#efe9e1]">
      <section className="min-h-dvh border border-[#ddd4c8] bg-[#f7f3ed]">
        <NorthpayHeader
          className="rounded-none border-0 border-b border-[#ddd4c8]"
          userName={displayName}
          initials={initials}
        />

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
                      Los usamos solo para verificar tu identidad y tu derecho a facturar desde
                      Colombia.
                    </p>
                  </div>
                </div>

                {/* Requirements list */}
                <div className="mt-6 flex items-center justify-between gap-3">
                  <p className="text-sm font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    Requisitos para Colombia
                  </p>
                  {stagedCount > 0 && !isUploading && (
                    <p className="text-sm font-semibold text-[#20af73]">
                      {stagedCount} de {DOC_REQUIREMENTS.length} listos 🎉
                    </p>
                  )}
                </div>

                <div className="mt-3 space-y-3">
                  {DOC_REQUIREMENTS.map(({ type, label, description, required, Icon }) => {
                    const file = staged[type];

                    // Subiendo (batch en curso)
                    if (isUploading && file) {
                      return (
                        <div
                          key={type}
                          className="rounded-2xl border border-[#e0d8cc] bg-white px-4 py-3"
                        >
                          <div className="flex items-center justify-between gap-3">
                            <div className="flex min-w-0 items-center gap-3">
                              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#d9e5f7] text-[#2d67d8]">
                                <Loader2 className="h-5 w-5 animate-spin" />
                              </div>
                              <div className="min-w-0">
                                <p className="truncate text-[1.4rem] leading-none font-semibold text-[#1f2740]">
                                  {file.name}
                                </p>
                                <p className="mt-1 truncate text-sm text-[#505a74]">
                                  Subiendo y verificando...
                                </p>
                              </div>
                            </div>
                            <span className="inline-flex items-center rounded-full bg-[#dce8ff] px-3 py-1 text-xs font-semibold text-[#2d67d8]">
                              ● Escaneando
                            </span>
                          </div>
                        </div>
                      );
                    }

                    // Archivo seleccionado (staged)
                    if (file) {
                      return (
                        <div
                          key={type}
                          className="rounded-2xl border border-[#c8e6d4] bg-[#f4fbf7] px-4 py-3"
                        >
                          <div className="flex items-center justify-between gap-3">
                            <div className="flex min-w-0 items-center gap-3">
                              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#bfead8] text-[#1f8d66]">
                                <BadgeCheck className="h-5 w-5" />
                              </div>
                              <div className="min-w-0">
                                <p className="truncate text-[1.4rem] leading-none font-semibold text-[#1f2740]">
                                  {file.name}
                                </p>
                                <p className="mt-1 truncate text-sm text-[#505a74]">{label}</p>
                              </div>
                            </div>
                            <div className="flex items-center gap-2">
                              <span className="inline-flex items-center rounded-full bg-[#d8f4e8] px-3 py-1 text-xs font-semibold text-[#1ea56d]">
                                ✓ Listo
                              </span>
                              <button
                                type="button"
                                onClick={() => fileInputRefs.current[type]?.click()}
                                className="h-9 rounded-xl border border-[#ddd3c5] bg-white px-4 text-sm font-semibold text-[#1f2740]"
                              >
                                Cambiar
                              </button>
                              <input
                                ref={(el) => {
                                  fileInputRefs.current[type] = el;
                                }}
                                type="file"
                                accept=".pdf,.jpg,.jpeg,.png"
                                className="hidden"
                                onChange={handleInputChange(type)}
                              />
                            </div>
                          </div>
                        </div>
                      );
                    }

                    // Pendiente
                    return (
                      <div
                        key={type}
                        className="rounded-2xl border border-dashed border-[#cfc5b6] bg-[#fbf9f4] px-4 py-3"
                      >
                        <input
                          ref={(el) => {
                            fileInputRefs.current[type] = el;
                          }}
                          type="file"
                          accept=".pdf,.jpg,.jpeg,.png"
                          className="hidden"
                          onChange={handleInputChange(type)}
                        />
                        <div className="flex items-center justify-between gap-3">
                          <div className="flex min-w-0 items-center gap-3">
                            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#ebe7dd] text-[#7f8597]">
                              <Icon className="h-5 w-5" />
                            </div>
                            <div className="min-w-0">
                              <p className="truncate text-[1.4rem] leading-none font-semibold text-[#6a728a]">
                                {label}
                              </p>
                              <p className="mt-1 truncate text-sm text-[#6a728a]">{description}</p>
                            </div>
                          </div>
                          <div className="flex items-center gap-2">
                            {!required && (
                              <span className="inline-flex rounded-full bg-[#ece7db] px-3 py-1 text-xs font-semibold text-[#7d8397]">
                                Opcional
                              </span>
                            )}
                            <button
                              type="button"
                              onClick={() => fileInputRefs.current[type]?.click()}
                              className="h-9 rounded-xl border border-[#ddd3c5] bg-white px-4 text-sm font-semibold text-[#1f2740]"
                            >
                              Elegir
                            </button>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>

                {uploadError && (
                  <div className="mt-4 rounded-2xl bg-[#fff5f3] px-4 py-3 text-sm text-[#d04f35]">
                    <span className="font-semibold">Error: </span>
                    {uploadError}
                  </div>
                )}

                <div className="mt-4 rounded-2xl bg-[#f2e6c5] px-4 py-3 text-sm text-[#3f4760]">
                  <span className="font-semibold">💡 Consejo:</span> los PDFs funcionan mejor. Si
                  usás el celular, asegurate de que se vean las 4 esquinas del documento.
                </div>
              </article>
            </section>

            <ProcessPaginationFooter
              className="rounded-none border-0 border-t border-[#ddd4c8] px-6"
              savedAt={savedAt}
              onBack={() => navigate("/personalData")}
              onNext={handleNext}
              nextDisabled={!requiredReady || isUploading}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName={fullName}
              steps={[
                { number: 1, label: "Datos personales", done: true },
                { number: 2, label: "Documentos", active: true },
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
