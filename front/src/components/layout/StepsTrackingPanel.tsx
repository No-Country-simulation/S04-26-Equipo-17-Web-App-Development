import { Check, FileText, MessageCircle } from "lucide-react";

type StepItem = {
  number: number;
  label: string;
  done?: boolean;
  active?: boolean;
};

type StepsTrackingPanelProps = {
  userName?: string;
  country?: string;
  countryCode?: string;
  currency?: string;
  steps?: StepItem[];
};

const defaultSteps: StepItem[] = [
  { number: 1, label: "Datos personales", done: true, active: true },
  { number: 2, label: "Documentos" },
  { number: 3, label: "Firma de contrato" },
  { number: 4, label: "Metodo de pago" },
  { number: 5, label: "Verificacion de identidad" },
];

export function StepsTrackingPanel({
  userName = "Sofia Restrepo",
  country = "Colombia",
  countryCode = "CO",
  currency = "COP",
  steps = defaultSteps,
}: StepsTrackingPanelProps) {
  return (
    <aside className="w-full rounded-[16px] bg-[#f7f3ed] p-4 sm:p-5">
      <p className="text-xs font-semibold tracking-wide text-[#7f8597] uppercase">ACTIVACION</p>
      <h3 className="mt-1 text-[2.1rem] leading-tight font-semibold text-[#1a2235]">{userName}</h3>
      <p className="mt-2 text-sm text-[#3f4760]">
        {countryCode} <span className="mx-1.5">{country}</span> · {currency}
      </p>

      <div className="mt-5 space-y-2.5">
        {steps.map((step) => (
          <div
            key={step.number}
            className={
              step.active
                ? "rounded-[14px] bg-white px-3 py-2.5"
                : "rounded-[12px] px-1.5 py-1.5"
            }
          >
            <div className="flex items-center gap-3">
              <div
                className={
                  step.done
                    ? "flex h-8 w-8 items-center justify-center rounded-[10px] bg-[#ccf0dd] text-[#1ea56d]"
                    : step.active
                      ? "flex h-8 w-8 items-center justify-center rounded-[10px] bg-[#e3e8f8] text-[#7c87a5]"
                    : "flex h-8 w-8 items-center justify-center rounded-[10px] bg-[#ece6da] text-sm font-semibold text-[#727a90]"
                }
              >
                {step.done ? <Check className="h-4 w-4" /> : step.active ? <FileText className="h-4 w-4" /> : step.number}
              </div>

              <p className="flex-1 text-[1rem] font-semibold text-[#1f2740]">{step.label}</p>

              {step.done && <span className="text-xs font-semibold text-[#1ea56d]">Listo</span>}
            </div>
          </div>
        ))}
      </div>
    </aside>
  );
}
