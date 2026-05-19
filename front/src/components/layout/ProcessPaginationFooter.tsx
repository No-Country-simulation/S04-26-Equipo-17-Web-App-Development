import { Button } from "@/components/ui/button";
import { ArrowLeft, ArrowRight } from "lucide-react";

type ProcessPaginationFooterProps = {
  savedLabel?: string;
  backLabel?: string;
  nextLabel?: string;
  onBack?: () => void;
  onNext?: () => void;
  className?: string;
};

export function ProcessPaginationFooter({
  savedLabel = "Guardado a las 14:36",
  backLabel = "Atras",
  nextLabel = "Continuar",
  onBack,
  onNext,
  className,
}: ProcessPaginationFooterProps) {
  return (
    <footer
      className={[
        "w-full rounded-[16px] border border-[#e6ddd2] bg-[#f7f3ed] px-5 py-3",
        "flex flex-wrap items-center justify-between gap-3",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
    >
      <Button
        type="button"
        variant="outline"
        onClick={onBack}
        className="h-11 rounded-[12px] border-[#e3d8cb] bg-[#f7f3ed] px-5 text-base font-semibold text-[#1f2740]"
      >
        <ArrowLeft className="mr-1 h-4 w-4" />
        {backLabel}
      </Button>

      <div className="flex items-center gap-3 sm:ml-auto">
        <p className="inline-flex items-center text-sm text-[#66708a]">
          <span className="mr-1.5 h-2 w-2 rounded-full bg-[#34c28c]" />
          {savedLabel}
        </p>

        <Button
          type="button"
          onClick={onNext}
          className="h-11 rounded-[12px] bg-[#182237] px-5 text-base font-semibold text-white hover:bg-[#12192a]"
        >
          {nextLabel}
          <ArrowRight className="ml-1 h-4 w-4" />
        </Button>
      </div>
    </footer>
  );
}
