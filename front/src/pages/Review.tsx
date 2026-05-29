import { useNavigate } from "react-router-dom";
import {
  ArrowRight,
  Check,
  Download,
  FileText,
  Landmark,
  MessageCircle,
  PenLine,
  ScanFace,
  User,
} from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { useApp } from "@/context/appContext";

// ─── Resumen de la solicitud ───────────────────────────────────────────────
// Pantalla terminal del onboarding (diseño pág. 10 "En revisión"). El backend
// no devuelve un resumen consolidado, así que los detalles se muestran como en
// el diseño; solo el nombre sale de la sesión real.

const SUMMARY: Array<{
  title: string;
  detail: string;
  Icon: React.ComponentType<{ className?: string }>;
}> = [
  { title: "Datos personales", detail: "Sofía Restrepo Quintero · CC 1014789332", Icon: User },
  { title: "Documentos", detail: "2 archivos · Verificados", Icon: FileText },
  { title: "Contrato", detail: "MSA-2089-COL · Firmado 14:39", Icon: PenLine },
  { title: "Pago", detail: "Bancolombia · COP", Icon: Landmark },
  { title: "Identidad", detail: "96% coincidencia · Vida OK", Icon: ScanFace },
];

function getInitials(name: string) {
  return name
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0] ?? "")
    .join("")
    .toUpperCase();
}

export default function Review() {
  const navigate = useNavigate();
  const { session } = useApp();

  const fullName = session?.fullName ?? "Usuario";
  const initials = getInitials(fullName);
  const displayName = fullName.split(" ").slice(0, 2).join(" ");
  const firstName = fullName.split(" ")[0] ?? fullName;

  return (
    <main className="min-h-dvh bg-[#efe9e1]">
      <section className="min-h-dvh border border-[#ddd4c8] bg-[#f7f3ed]">
        <NorthpayHeader
          className="rounded-none border-0 border-b border-[#ddd4c8]"
          statusLabel="En revisión"
          userName={displayName}
          initials={initials}
        />

        <div className="grid min-h-[calc(100dvh-74px)] items-start gap-8 px-6 py-8 lg:grid-cols-[1.1fr_1fr] lg:px-10">
          {/* ─── Columna izquierda: estado + Mara ────────────────────────── */}
          <div>
            <span className="inline-flex items-center rounded-full bg-[#dce9ff] px-3 py-1 text-xs font-semibold text-[#2157c9]">
              <span className="mr-1.5 h-1.5 w-1.5 rounded-full bg-[#2157c9]" />
              Enviado · 30 abr 14:46
            </span>

            <h1 className="mt-4 text-[2.6rem] leading-tight font-bold tracking-tight text-[#1b2234]">
              ¡Buen trabajo, {firstName}! 🎉
              <br />
              <span className="text-[#9aa0b4]">Listo para revisión.</span>
            </h1>

            <p className="mt-3 max-w-[58ch] text-[1.1rem] leading-relaxed text-[#3f4760]">
              Una especialista de Northpay está revisando tu solicitud. La mayoría termina en menos
              de <span className="font-semibold text-[#ff7a59]">4 horas hábiles</span> y te
              avisaremos por correo.
            </p>

            {/* Tarjeta de Mara */}
            <div className="mt-6 rounded-2xl border border-[#ddd0fb] bg-[#ede9fb] p-5">
              <div className="flex items-center gap-3">
                <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-[#b8a4f0] text-sm font-bold text-white">
                  MV
                </span>
                <div>
                  <p className="text-base font-bold text-[#1f2740]">Mara Velasco</p>
                  <p className="text-sm text-[#6c63a0]">Especialista de activación · LATAM</p>
                </div>
              </div>
              <p className="mt-3 inline-flex items-start gap-1.5 text-sm leading-relaxed text-[#5b4baa]">
                <MessageCircle className="mt-0.5 h-4 w-4 shrink-0" />
                «Tengo tu solicitud en pantalla. Si necesito algo te aviso por aquí mismo.»
              </p>
            </div>

            {/* Acciones */}
            <div className="mt-6 flex flex-wrap gap-3">
              <button
                type="button"
                onClick={() => navigate("/wizard")}
                className="inline-flex h-11 items-center gap-1.5 rounded-[12px] bg-[#182237] px-5 text-base font-semibold text-white hover:bg-[#12192a]"
              >
                Abrir tu panel
                <ArrowRight className="h-4 w-4" />
              </button>
              <button
                type="button"
                className="inline-flex h-11 items-center gap-1.5 rounded-[12px] border border-[#e3d8cb] bg-white px-5 text-base font-semibold text-[#1f2740] hover:border-[#b8b0a5]"
              >
                <Download className="h-4 w-4" />
                Descargar PDF
              </button>
            </div>
          </div>

          {/* ─── Columna derecha: resumen ────────────────────────────────── */}
          <div>
            <p className="mb-3 border-l-2 border-[#ff7a59] pl-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
              Resumen de tu solicitud
            </p>

            <div className="space-y-3">
              {SUMMARY.map(({ title, detail, Icon }) => (
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
                      <p className="mt-0.5 truncate text-xs text-[#6a728a]">{detail}</p>
                    </div>
                  </div>
                  <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-[#34c28c] text-white">
                    <Check className="h-3.5 w-3.5" />
                  </span>
                </div>
              ))}
            </div>

            {/* Activación estimada — card oscura */}
            <div className="mt-4 rounded-2xl bg-[#1a2035] px-5 py-4">
              <div className="flex items-end justify-between gap-4">
                <div>
                  <p className="text-xs font-semibold tracking-[0.1em] text-[#8b97b8] uppercase">
                    Activación estimada
                  </p>
                  <p className="mt-1 text-[2rem] leading-none font-bold text-white">~ 4 horas</p>
                </div>
                <p className="pb-1 text-xs text-[#8b97b8]">hoy, antes de las 19:00</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
