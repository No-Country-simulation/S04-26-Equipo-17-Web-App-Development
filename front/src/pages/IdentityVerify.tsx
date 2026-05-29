import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Camera,
  Check,
  Globe,
  Loader2,
  Lock,
  RotateCcw,
  ScanFace,
  ShieldCheck,
  Sparkles,
  Target,
} from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import { useApp } from "@/context/appContext";
import { ApiError } from "@/lib/api";
import { useUploadSelfie } from "@/lib/queries";

// ─── Comprobaciones ────────────────────────────────────────────────────────
// Son los resultados que devuelve el proveedor KYC (autenticidad de documento,
// liveness, listas de sanciones). No hay endpoint que los alimente en el front,
// así que se renderizan como los muestra el diseño: presentacionales.

type CheckStatus = "approved" | "verifying" | "queued";

const CHECKS: Array<{
  title: string;
  description: string;
  status: CheckStatus;
  Icon: React.ComponentType<{ className?: string }>;
}> = [
  {
    title: "Autenticidad del documento",
    description: "Cédula · Colombia · MRZ válido",
    status: "approved",
    Icon: ShieldCheck,
  },
  {
    title: "Coincidencia facial",
    description: "Foto de cédula ↔ captura · 96%",
    status: "approved",
    Icon: Target,
  },
  {
    title: "Detección de vida",
    description: "Capturando 3 micro-movimientos",
    status: "verifying",
    Icon: Sparkles,
  },
  {
    title: "Listas de sanciones",
    description: "OFAC, UE, ONU",
    status: "queued",
    Icon: Globe,
  },
];

const STATUS_PILL: Record<CheckStatus, { label: string; className: string }> = {
  approved: { label: "Aprobado", className: "bg-[#d8f4e8] text-[#1ea56d]" },
  verifying: { label: "Verificando", className: "bg-[#dce8ff] text-[#2157c9]" },
  queued: { label: "En cola", className: "bg-[#ece6da] text-[#7d8397]" },
};

// ─── Helpers ─────────────────────────────────────────────────────────────────

function getInitials(name: string) {
  return name
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0] ?? "")
    .join("")
    .toUpperCase();
}

type CameraState = "idle" | "active" | "captured" | "error";

// ─── Main page ────────────────────────────────────────────────────────────────

export default function IdentityVerify() {
  const navigate = useNavigate();
  const { session, savedAt, clearSession } = useApp();
  const selfieMutation = useUploadSelfie();

  const videoRef = useRef<HTMLVideoElement | null>(null);
  const streamRef = useRef<MediaStream | null>(null);

  const [cameraState, setCameraState] = useState<CameraState>("idle");
  const [capturedFile, setCapturedFile] = useState<File | null>(null);
  const [capturedPreview, setCapturedPreview] = useState<string | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const fullName = session?.fullName ?? "Usuario";
  const initials = getInitials(fullName);
  const displayName = fullName.split(" ").slice(0, 2).join(" ");

  // Apaga la webcam y libera los tracks. Se llama al capturar y al desmontar.
  const stopStream = useCallback(() => {
    streamRef.current?.getTracks().forEach((track) => track.stop());
    streamRef.current = null;
  }, []);

  // Pide acceso a la cámara frontal y muestra el feed en vivo.
  const startCamera = useCallback(async () => {
    setSubmitError(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "user" },
        audio: false,
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        await videoRef.current.play();
      }
      setCameraState("active");
    } catch {
      setCameraState("error");
    }
  }, []);

  // Toma un frame del video, lo convierte en File y apaga la cámara.
  const capture = useCallback(() => {
    const video = videoRef.current;
    if (!video) return;

    const canvas = document.createElement("canvas");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    canvas.toBlob((blob) => {
      if (!blob) return;
      const file = new File([blob], "selfie.jpg", { type: "image/jpeg" });
      setCapturedFile(file);
      setCapturedPreview(URL.createObjectURL(blob));
      setCameraState("captured");
      stopStream();
    }, "image/jpeg", 0.92);
  }, [stopStream]);

  // Descarta la captura y vuelve a encender la cámara.
  const retake = useCallback(() => {
    if (capturedPreview) URL.revokeObjectURL(capturedPreview);
    setCapturedFile(null);
    setCapturedPreview(null);
    void startCamera();
  }, [capturedPreview, startCamera]);

  // Limpieza: si el usuario se va con la cámara prendida, la apagamos.
  useEffect(() => {
    return () => {
      stopStream();
      if (capturedPreview) URL.revokeObjectURL(capturedPreview);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSubmit = () => {
    if (!capturedFile) return;
    setSubmitError(null);
    selfieMutation.mutate(capturedFile, {
      onSuccess: () => navigate("/review"),
      onError: (err) => {
        if (err instanceof ApiError && err.status === 401) {
          clearSession();
          navigate("/");
          return;
        }
        setSubmitError(err instanceof Error ? err.message : "No pudimos enviar tu verificación");
      },
    });
  };

  const isSubmitting = selfieMutation.isPending;

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
              currentStep={5}
              totalSteps={5}
              minutesRemaining="~ 1 min"
              className="px-6 pt-6"
            />

            <section className="relative mt-4 flex-1 overflow-y-auto px-6 pb-6">
              <article>
                {/* Heading */}
                <div className="flex items-start gap-4">
                  <div className="mt-0.5 flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#dce8ff] text-[#2563d9]">
                    <ScanFace className="h-5 w-5" />
                  </div>
                  <div>
                    <h1 className="text-[2.5rem] leading-tight font-bold tracking-tight text-[#1b2234]">
                      Último paso, ¡una selfie rápida! 🪪
                    </h1>
                    <p className="mt-1 max-w-[66ch] text-[1.15rem] leading-relaxed text-[#3f4760]">
                      30 segundos para confirmar que eres tú. Nunca guardamos el video, solo el
                      resultado.
                    </p>
                  </div>
                </div>

                <div className="mt-6 grid gap-5 lg:grid-cols-2">
                  {/* ─── Captura en vivo ─────────────────────────────────── */}
                  <div>
                    <p className="mb-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                      Captura en vivo
                    </p>

                    <div className="relative aspect-[4/5] overflow-hidden rounded-2xl bg-[#14182a]">
                      {/* Video en vivo (oculto hasta que la cámara esté activa) */}
                      <video
                        ref={videoRef}
                        muted
                        playsInline
                        className={[
                          "h-full w-full object-cover",
                          cameraState === "active" ? "block" : "hidden",
                        ].join(" ")}
                      />

                      {/* Preview de la captura tomada */}
                      {cameraState === "captured" && capturedPreview && (
                        <img
                          src={capturedPreview}
                          alt="Tu selfie"
                          className="h-full w-full object-cover"
                        />
                      )}

                      {/* Óvalo guía (coral) — sobre el video o el preview */}
                      {(cameraState === "active" || cameraState === "captured") && (
                        <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
                          <div className="h-[78%] w-[64%] rounded-[50%] border-[3px] border-[#ff7a59]/90 shadow-[0_0_0_9999px_rgba(20,24,42,0.45)]" />
                        </div>
                      )}

                      {/* Estado activo: indicadores grabando / score / instrucción */}
                      {cameraState === "active" && (
                        <>
                          <span className="absolute top-3 left-3 inline-flex items-center rounded-full bg-black/45 px-2.5 py-1 text-xs font-semibold text-white backdrop-blur-sm">
                            <span className="mr-1.5 h-1.5 w-1.5 animate-pulse rounded-full bg-[#ff7a59]" />
                            Grabando
                          </span>
                          <span className="absolute top-3 right-3 inline-flex items-center rounded-full bg-black/45 px-2.5 py-1 font-mono text-xs font-semibold text-white backdrop-blur-sm">
                            Rostro 0,94
                          </span>
                          <div className="absolute right-0 bottom-4 left-0 px-4 text-center">
                            <p className="text-xs font-semibold tracking-wide text-[#ff7a59] uppercase">
                              Paso 2 de 3
                            </p>
                            <p className="mt-0.5 text-base font-semibold text-white">
                              Gira la cabeza a la derecha →
                            </p>
                          </div>
                        </>
                      )}

                      {/* Estado inicial: botón para encender la cámara */}
                      {cameraState === "idle" && (
                        <div className="absolute inset-0 flex flex-col items-center justify-center gap-4 px-6 text-center">
                          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-white/10 text-white">
                            <Camera className="h-6 w-6" />
                          </div>
                          <p className="text-sm text-white/70">
                            Necesitamos tu cámara para una selfie rápida.
                          </p>
                          <button
                            type="button"
                            onClick={() => void startCamera()}
                            className="h-11 rounded-[12px] bg-white px-5 text-sm font-semibold text-[#182237] hover:bg-white/90"
                          >
                            Activar cámara
                          </button>
                        </div>
                      )}

                      {/* Estado de error de permisos */}
                      {cameraState === "error" && (
                        <div className="absolute inset-0 flex flex-col items-center justify-center gap-4 px-6 text-center">
                          <p className="text-sm text-white/80">
                            No pudimos acceder a tu cámara. Revisá los permisos del navegador e
                            intentá de nuevo.
                          </p>
                          <button
                            type="button"
                            onClick={() => void startCamera()}
                            className="h-10 rounded-[12px] bg-white px-4 text-sm font-semibold text-[#182237] hover:bg-white/90"
                          >
                            Reintentar
                          </button>
                        </div>
                      )}
                    </div>

                    {/* Acción bajo la cámara */}
                    {cameraState === "active" && (
                      <button
                        type="button"
                        onClick={capture}
                        className="mt-3 inline-flex h-11 w-full items-center justify-center gap-2 rounded-[12px] bg-[#182237] text-sm font-semibold text-white hover:bg-[#12192a]"
                      >
                        <Camera className="h-4 w-4" />
                        Capturar selfie
                      </button>
                    )}

                    {cameraState === "captured" && (
                      <button
                        type="button"
                        onClick={retake}
                        className="mt-3 inline-flex h-11 w-full items-center justify-center gap-2 rounded-[12px] border border-[#e3d8cb] bg-white text-sm font-semibold text-[#1f2740] hover:border-[#b8b0a5]"
                      >
                        <RotateCcw className="h-4 w-4" />
                        Volver a tomar
                      </button>
                    )}

                    <p className="mt-3 inline-flex items-center gap-1.5 text-xs text-[#6f7892]">
                      <Lock className="h-3 w-3" />
                      Procesado en tu navegador · ISO/IEC 30107-3 anti-spoofing
                    </p>
                  </div>

                  {/* ─── Comprobaciones ──────────────────────────────────── */}
                  <div>
                    <p className="mb-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                      Comprobaciones
                    </p>

                    <div className="space-y-3">
                      {CHECKS.map(({ title, description, status, Icon }) => {
                        const pill = STATUS_PILL[status];
                        return (
                          <div
                            key={title}
                            className="flex items-center justify-between gap-3 rounded-2xl border border-[#e6ddd2] bg-white px-4 py-3"
                          >
                            <div className="flex min-w-0 items-center gap-3">
                              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#f0ebe4] text-[#39425d]">
                                <Icon className="h-5 w-5" />
                              </div>
                              <div className="min-w-0">
                                <p className="truncate text-sm font-bold text-[#1f2740]">{title}</p>
                                <p className="mt-0.5 truncate text-xs text-[#6a728a]">
                                  {description}
                                </p>
                              </div>
                            </div>
                            <span
                              className={[
                                "inline-flex shrink-0 items-center gap-1 rounded-full px-3 py-1 text-xs font-semibold",
                                pill.className,
                              ].join(" ")}
                            >
                              {status === "approved" && <Check className="h-3 w-3" />}
                              {status === "verifying" && (
                                <Loader2 className="h-3 w-3 animate-spin" />
                              )}
                              {pill.label}
                            </span>
                          </div>
                        );
                      })}
                    </div>

                    {/* Caja de privacidad (lila) */}
                    <div className="mt-3 rounded-2xl border border-[#ddd0fb] bg-[#ede9fb] px-4 py-3">
                      <p className="inline-flex items-center gap-1.5 text-sm font-semibold text-[#5b4baa]">
                        <Lock className="h-3.5 w-3.5" />
                        Tu privacidad es prioridad
                      </p>
                      <p className="mt-1 text-xs leading-relaxed text-[#6c63a0]">
                        Solo guardamos un hash cifrado del resultado. El video nunca sale de tu
                        dispositivo.
                      </p>
                    </div>
                  </div>
                </div>

                {/* Error de envío */}
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
              nextLabel="Enviar para revisión"
              onBack={() => navigate("/payment")}
              onNext={handleSubmit}
              nextDisabled={!capturedFile || isSubmitting}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName={fullName}
              steps={[
                { number: 1, label: "Datos personales", done: true },
                { number: 2, label: "Documentos", done: true },
                { number: 3, label: "Firma de contrato", done: true },
                { number: 4, label: "Método de pago", done: true },
                { number: 5, label: "Verificación de identidad", active: true },
              ]}
            />
          </aside>
        </div>
      </section>
    </main>
  );
}
