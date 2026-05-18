import { Lock } from "lucide-react";

type ProcessStatusBarProps = {
  currentStep: number;
  totalSteps: number;
  minutesRemaining?: string;
  securityLabel?: string;
  className?: string;
};

function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max);
}

export function ProcessStatusBar({
  currentStep,
  totalSteps,
  minutesRemaining = "~ 5 min",
  securityLabel = "Cifrado de extremo a extremo",
  className,
}: ProcessStatusBarProps) {
  const safeTotal = Math.max(totalSteps, 1);
  const safeCurrent = clamp(currentStep, 0, safeTotal);
  const progress = Math.round((safeCurrent / safeTotal) * 100);

  return (
    <section className={className}>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="inline-flex items-center rounded-full bg-[#dce9ff] px-3 py-1 text-xs font-semibold text-[#2157c9]">
            <span className="mr-1.5 h-1.5 w-1.5 rounded-full bg-[#2157c9]" />
            Paso {safeCurrent} de {safeTotal}
          </span>
          <span className="text-sm font-medium text-[#5c6681]">{minutesRemaining}</span>
        </div>

        <span className="inline-flex items-center gap-1.5 text-sm font-medium text-[#6f7892]">
          <Lock className="h-3.5 w-3.5" />
          {securityLabel}
        </span>
      </div>

      <div className="mt-4 flex items-center gap-3">
        <div className="relative h-2 w-full overflow-hidden rounded-full bg-[#e6e0d6]">
          <div
            className="h-full rounded-full bg-[linear-gradient(90deg,#2d67d8_0%,#7f78c7_58%,#f07a60_100%)] transition-all duration-300"
            style={{ width: `${progress}%` }}
          />
        </div>
        <span className="min-w-9 text-sm font-semibold text-[#39425d]">{progress}%</span>
      </div>
    </section>
  );
}
