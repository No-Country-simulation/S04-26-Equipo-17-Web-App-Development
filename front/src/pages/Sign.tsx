import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FileBadge2, Loader2 } from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import { useApp } from "@/context/appContext";
import { api, ApiError } from "@/lib/api";
import { useSignContract } from "@/lib/queries";

export default function Sign() {
  const navigate = useNavigate();
  const { session, savedAt, clearSession } = useApp();
  const signMutation = useSignContract();

  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [pdfLoading, setPdfLoading] = useState(true);
  const [pdfError, setPdfError] = useState<string | null>(null);
  const [accepted, setAccepted] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    let objectUrl: string;

    api
      .blob(`/api/onboarding/${session.onboardingId}/contract-preview`, session.sessionToken)
      .then((blob) => {
        objectUrl = URL.createObjectURL(blob);
        setPdfUrl(objectUrl);
      })
      .catch((err) => {
        setPdfError(err instanceof Error ? err.message : "Error al cargar el contrato");
      })
      .finally(() => setPdfLoading(false));

    return () => {
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [session]);

  const handleSign = () => {
    setSubmitError(null);
    signMutation.mutate(undefined, {
      onSuccess: () => navigate("/payment"),
      onError: (err) => {
        if (err instanceof ApiError && err.status === 401) {
          clearSession();
          navigate("/");
          return;
        }
        setSubmitError(err instanceof Error ? err.message : "Error al firmar el contrato");
      },
    });
  };

  const fullName = session?.fullName ?? "Usuario";

  return (
    <main className="min-h-dvh bg-[#efe9e1]">
      <section className="min-h-dvh border border-[#ddd4c8] bg-[#f7f3ed]">
        <NorthpayHeader
          className="rounded-none border-0 border-b border-[#ddd4c8]"
          userName={fullName.split(" ").slice(0, 2).join(" ")}
          initials={fullName
            .split(" ")
            .slice(0, 2)
            .map((w) => w[0] ?? "")
            .join("")
            .toUpperCase()}
        />

        <div className="grid min-h-[calc(100dvh-74px)] xl:grid-cols-[1fr_290px]">
          <div className="flex min-h-0 flex-col">
            <ProcessStatusBar
              currentStep={3}
              totalSteps={5}
              minutesRemaining="~ 3 min"
              className="px-6 pt-6"
            />

            <section className="relative mt-4 flex-1 overflow-y-auto px-6 pb-6">
              <article>
                <div className="flex items-start gap-4">
                  <div className="mt-0.5 flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#f6dbd1] text-[#c97158]">
                    <FileBadge2 className="h-5 w-5" />
                  </div>
                  <div>
                    <h1 className="text-[2.5rem] leading-tight font-bold tracking-tight text-[#1b2234]">
                      Revisá y firmá el contrato
                    </h1>
                    <p className="mt-1 max-w-[66ch] text-[1.15rem] leading-relaxed text-[#3f4760]">
                      Leé el contrato con detenimiento. Una vez que lo firmes, quedará registrado.
                    </p>
                  </div>
                </div>

                <div className="mt-6">
                  {pdfLoading && (
                    <div className="flex h-[500px] items-center justify-center rounded-2xl border border-[#e0d8cc] bg-white">
                      <div className="flex flex-col items-center gap-3 text-[#7a8094]">
                        <Loader2 className="h-8 w-8 animate-spin" />
                        <p className="text-sm">Cargando contrato...</p>
                      </div>
                    </div>
                  )}

                  {pdfError && (
                    <div className="flex h-[200px] items-center justify-center rounded-2xl border border-[#f5c6bc] bg-[#fff5f3]">
                      <p className="text-sm text-[#d04f35]">{pdfError}</p>
                    </div>
                  )}

                  {pdfUrl && (
                    <iframe
                      src={pdfUrl}
                      className="h-[500px] w-full rounded-2xl border border-[#e0d8cc]"
                      title="Contrato"
                    />
                  )}
                </div>

                <label className="mt-4 flex cursor-pointer items-center gap-3 rounded-2xl border border-[#e0d8cc] bg-white px-4 py-3">
                  <input
                    type="checkbox"
                    checked={accepted}
                    onChange={(e) => setAccepted(e.target.checked)}
                    className="h-4 w-4 accent-[#182237]"
                  />
                  <span className="text-sm font-semibold text-[#1b2234]">
                    Leí y acepto los términos del contrato
                  </span>
                </label>

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
              onBack={() => navigate("/documents")}
              onNext={handleSign}
              nextDisabled={!accepted || signMutation.isPending}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName={fullName}
              steps={[
                { number: 1, label: "Datos personales", done: true },
                { number: 2, label: "Documentos", done: true },
                { number: 3, label: "Firma de contrato", active: true },
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
