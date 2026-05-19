import { cn } from "@/lib/utils";

type NorthpayHeaderProps = {
  className?: string;
  statusLabel?: string;
  countryCode?: string;
  userName?: string;
  initials?: string;
};

export function NorthpayHeader({
  className,
  statusLabel = "En curso",
  countryCode = "CO",
  userName = "Sofia R.",
  initials = "SR",
}: NorthpayHeaderProps) {
  return (
    <header
      className={cn(
        "w-full rounded-[16px] border border-[#e6ddd2] bg-[#f7f3ed] px-5 py-3",
        className,
      )}
    >
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="flex h-6 w-6 items-center justify-center rounded-full bg-[#2d67d8] text-[0.62rem] font-bold text-white">
            N
          </div>
          <p className="text-[2rem] leading-none font-semibold tracking-tight text-[#182134]">
            Northpay
          </p>
        </div>

        <div className="flex items-center gap-3 text-sm text-[#1d2639]">
          <span className="inline-flex items-center rounded-full bg-[#dce9ff] px-3 py-1 text-xs font-semibold text-[#2157c9]">
            <span className="mr-1.5 h-1.5 w-1.5 rounded-full bg-[#2157c9]" />
            {statusLabel}
          </span>

          <span className="font-medium text-[#3c455d]">{countryCode}</span>
          <span className="font-medium text-[#1f293d]">{userName}</span>

          <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-[#ffd8cb] text-[0.68rem] font-semibold text-[#f06f56]">
            {initials}
          </span>
        </div>
      </div>
    </header>
  );
}
