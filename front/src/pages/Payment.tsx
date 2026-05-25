import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Building2, CreditCard, Wallet, Zap } from "lucide-react";

import { NorthpayHeader } from "@/components/layout/NorthpayHeader";
import { ProcessPaginationFooter } from "@/components/layout/ProcessPaginationFooter";
import { ProcessStatusBar } from "@/components/layout/ProcessStatusBar";
import { StepsTrackingPanel } from "@/components/layout/StepsTrackingPanel";
import { useApp } from "@/context/appContext";
import { useConfigurePayment } from "@/lib/queries";

type Currency = "USD" | "COP" | "EUR" | "USDC";
type PaymentMethod = "BANK_TRANSFER" | "WISE" | "USDC";

const CURRENCIES: Array<{ id: Currency; flag: string; label: string }> = [
  { id: "USD", flag: "US", label: "USD" },
  { id: "COP", flag: "CO", label: "COP" },
  { id: "EUR", flag: "EU", label: "EUR" },
  { id: "USDC", flag: "₮", label: "USDC" },
];

const PAYMENT_METHODS: Array<{
  id: PaymentMethod;
  title: string;
  subtitle: string;
  time: string;
  cost: string;
  Icon: React.ComponentType<{ className?: string }>;
}> = [
  {
    id: "BANK_TRANSFER",
    title: "Transferencia local",
    subtitle: "Bancolombia, Davvienda...",
    time: "Mismo día",
    cost: "COP 0",
    Icon: Building2,
  },
  {
    id: "WISE",
    title: "Wise",
    subtitle: "Cartera multidivisa",
    time: "1 día",
    cost: "1.5%",
    Icon: CreditCard,
  },
  {
    id: "USDC",
    title: "USDC on-chain",
    subtitle: "Polygon · Base",
    time: "Minutos",
    cost: "gas: red",
    Icon: Zap,
  },
];

const RATE_INFO: Record<Currency, { amount: string; rateLabel: string }> = {
  USD: { amount: "USD 5.200", rateLabel: "USD · tasa de referencia" },
  COP: { amount: "COP 21.304.000", rateLabel: "USD/COP · últimas 30 días" },
  EUR: { amount: "EUR 4.800", rateLabel: "USD/EUR · últimas 30 días" },
  USDC: { amount: "USDC 5.200", rateLabel: "Paridad 1:1 con USD" },
};

const CHART_BARS = [40, 55, 45, 70, 60, 80, 65, 90, 75, 100];

// ─── Sub-forms ───────────────────────────────────────────────────────────────

interface BankDetails {
  bankName: string;
  accountNumber: string;
  accountType: "SAVINGS" | "CHECKING";
}

function BankTransferForm({
  value,
  onChange,
}: {
  value: BankDetails;
  onChange: (v: BankDetails) => void;
}) {
  return (
    <div className="mt-4 space-y-3 rounded-2xl border border-[#ddd4c8] bg-white p-4">
      <div>
        <label className="mb-1.5 block text-sm font-semibold text-[#39425d]">Banco</label>
        <input
          className="w-full rounded-xl border border-[#ddd3c5] bg-[#faf9f7] px-4 py-2.5 text-sm text-[#1f2740] placeholder-[#9ba5bd] outline-none focus:border-[#2563d9] focus:bg-white"
          placeholder="Ej: Bancolombia"
          value={value.bankName}
          onChange={(e) => onChange({ ...value, bankName: e.target.value })}
        />
      </div>
      <div>
        <label className="mb-1.5 block text-sm font-semibold text-[#39425d]">
          Número de cuenta
        </label>
        <input
          className="w-full rounded-xl border border-[#ddd3c5] bg-[#faf9f7] px-4 py-2.5 font-mono text-sm text-[#1f2740] placeholder-[#9ba5bd] outline-none focus:border-[#2563d9] focus:bg-white"
          placeholder="0000 0000 0000 0000"
          value={value.accountNumber}
          onChange={(e) => onChange({ ...value, accountNumber: e.target.value })}
        />
      </div>
      <div>
        <label className="mb-1.5 block text-sm font-semibold text-[#39425d]">Tipo de cuenta</label>
        <div className="flex gap-2">
          {(["SAVINGS", "CHECKING"] as const).map((type) => (
            <button
              key={type}
              type="button"
              onClick={() => onChange({ ...value, accountType: type })}
              className={[
                "flex-1 rounded-xl border px-4 py-2.5 text-sm font-semibold transition-colors",
                value.accountType === type
                  ? "border-[#2563d9] bg-[#dce8ff] text-[#2157c9]"
                  : "border-[#ddd3c5] bg-white text-[#1f2740] hover:border-[#b5c5e8]",
              ].join(" ")}
            >
              {type === "SAVINGS" ? "Ahorros" : "Corriente"}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

interface WiseDetails {
  email: string;
}

function WiseForm({
  value,
  onChange,
}: {
  value: WiseDetails;
  onChange: (v: WiseDetails) => void;
}) {
  return (
    <div className="mt-4 space-y-3 rounded-2xl border border-[#ddd4c8] bg-white p-4">
      <div>
        <label className="mb-1.5 block text-sm font-semibold text-[#39425d]">
          Email de tu cuenta Wise
        </label>
        <input
          type="email"
          className="w-full rounded-xl border border-[#ddd3c5] bg-[#faf9f7] px-4 py-2.5 text-sm text-[#1f2740] placeholder-[#9ba5bd] outline-none focus:border-[#2563d9] focus:bg-white"
          placeholder="tu@email.com"
          value={value.email}
          onChange={(e) => onChange({ email: e.target.value })}
        />
      </div>
    </div>
  );
}

interface UsdcDetails {
  walletAddress: string;
  network: string;
}

function UsdcForm({
  value,
  onChange,
}: {
  value: UsdcDetails;
  onChange: (v: UsdcDetails) => void;
}) {
  return (
    <div className="mt-4 space-y-3 rounded-2xl border border-[#ddd4c8] bg-white p-4">
      <div>
        <label className="mb-1.5 block text-sm font-semibold text-[#39425d]">
          Dirección de billetera (EVM)
        </label>
        <input
          className="w-full rounded-xl border border-[#ddd3c5] bg-[#faf9f7] px-4 py-2.5 font-mono text-sm text-[#1f2740] placeholder-[#9ba5bd] outline-none focus:border-[#2563d9] focus:bg-white"
          placeholder="0x..."
          value={value.walletAddress}
          onChange={(e) => onChange({ ...value, walletAddress: e.target.value })}
        />
      </div>
      <div>
        <label className="mb-1.5 block text-sm font-semibold text-[#39425d]">Red</label>
        <div className="flex gap-2">
          {["Polygon", "Base"].map((network) => (
            <button
              key={network}
              type="button"
              onClick={() => onChange({ ...value, network })}
              className={[
                "flex-1 rounded-xl border px-4 py-2.5 text-sm font-semibold transition-colors",
                value.network === network
                  ? "border-[#2563d9] bg-[#dce8ff] text-[#2157c9]"
                  : "border-[#ddd3c5] bg-white text-[#1f2740] hover:border-[#b5c5e8]",
              ].join(" ")}
            >
              {network}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

function getInitials(name: string) {
  return name
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0] ?? "")
    .join("")
    .toUpperCase();
}

// ─── Main page ────────────────────────────────────────────────────────────────

export default function Payment() {
  const navigate = useNavigate();
  const { session, savedAt } = useApp();
  const paymentMutation = useConfigurePayment();

  const [currency, setCurrency] = useState<Currency>("COP");
  const [method, setMethod] = useState<PaymentMethod | null>(null);
  const [bankDetails, setBankDetails] = useState<BankDetails>({
    bankName: "",
    accountNumber: "",
    accountType: "SAVINGS",
  });
  const [wiseDetails, setWiseDetails] = useState<WiseDetails>({ email: "" });
  const [usdcDetails, setUsdcDetails] = useState<UsdcDetails>({
    walletAddress: "",
    network: "Polygon",
  });
  const [submitError, setSubmitError] = useState<string | null>(null);

  const fullName = session?.fullName ?? "Usuario";
  const initials = getInitials(fullName);
  const displayName = fullName.split(" ").slice(0, 2).join(" ");

  const isValid = () => {
    if (!method) return false;
    if (method === "BANK_TRANSFER")
      return bankDetails.bankName.trim().length > 0 && bankDetails.accountNumber.trim().length > 0;
    if (method === "WISE") return wiseDetails.email.includes("@");
    if (method === "USDC")
      return usdcDetails.walletAddress.startsWith("0x") && usdcDetails.walletAddress.length >= 42;
    return false;
  };

  const buildDetails = (): Record<string, unknown> => {
    const base = { currency };
    if (method === "BANK_TRANSFER") return { ...base, ...bankDetails };
    if (method === "WISE") return { ...base, ...wiseDetails };
    if (method === "USDC") return { ...base, ...usdcDetails };
    return base;
  };

  const handleNext = () => {
    if (!method) return;
    setSubmitError(null);
    paymentMutation.mutate(
      { accountType: method, details: buildDetails() },
      {
        onSuccess: () => navigate("/identityVerify"),
        onError: (err) => {
          setSubmitError(err instanceof Error ? err.message : "Error al configurar el pago");
        },
      },
    );
  };

  const isSubmitting = paymentMutation.isPending;
  const rate = RATE_INFO[currency];

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
              currentStep={4}
              totalSteps={5}
              minutesRemaining="~ 2 min"
              className="px-6 pt-6"
            />

            <section className="relative mt-4 flex-1 overflow-y-auto px-6 pb-6">
              <article>
                {/* Heading */}
                <div className="flex items-start gap-4">
                  <div className="mt-0.5 flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-[#dce8ff] text-[#2563d9]">
                    <Wallet className="h-5 w-5" />
                  </div>
                  <div>
                    <h1 className="text-[2.5rem] leading-tight font-bold tracking-tight text-[#1b2234]">
                      ¿Cómo te pagamos?
                    </h1>
                    <p className="mt-1 max-w-[66ch] text-[1.15rem] leading-relaxed text-[#3f4760]">
                      Elegí lo que más te convenga. Podés cambiarlo cuando quieras y verás el tipo
                      de cambio antes de cada pago.
                    </p>
                  </div>
                </div>

                {/* Estimated amount — dark card */}
                <div className="mt-6 rounded-2xl bg-[#1a2035] px-5 py-5 text-white">
                  <p className="text-xs font-semibold tracking-[0.1em] text-[#8b97b8] uppercase">
                    Recibirás (estimado)
                  </p>
                  <div className="mt-2 flex items-end justify-between gap-4">
                    <div>
                      <p className="text-[2.2rem] leading-none font-bold tracking-tight">
                        {rate.amount}{" "}
                        <span className="text-xl font-semibold text-[#8b97b8]">/mes</span>
                      </p>
                      <p className="mt-2 text-xs text-[#6b7ba0]">
                        Equivalente a USD 5.200 · Tasa: 1 USD = 4.09130 COP · Comisión FK 0.4%
                      </p>
                    </div>
                    {/* Mini sparkline */}
                    <div className="flex h-12 shrink-0 items-end gap-0.5 opacity-60">
                      {CHART_BARS.map((h, i) => (
                        <div
                          key={i}
                          className="w-1.5 rounded-sm bg-[#34c28c]"
                          style={{ height: `${h}%` }}
                        />
                      ))}
                    </div>
                  </div>
                  <p className="mt-1 text-xs text-[#6b7ba0]">{rate.rateLabel}</p>
                </div>

                {/* Currency selector */}
                <div className="mt-5">
                  <p className="mb-2 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    Recibir en
                  </p>
                  <div className="inline-flex gap-1 rounded-2xl border border-[#ddd4c8] bg-[#f0ebe4] p-1">
                    {CURRENCIES.map(({ id, flag, label }) => (
                      <button
                        key={id}
                        type="button"
                        onClick={() => setCurrency(id)}
                        className={[
                          "rounded-xl px-4 py-2 text-sm font-semibold transition-all",
                          currency === id
                            ? "bg-white text-[#1f2740] shadow-sm"
                            : "text-[#49526d] hover:text-[#1f2740]",
                        ].join(" ")}
                      >
                        <span className="mr-1.5 text-xs text-[#8b97b8]">{flag}</span>
                        {label}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Payment methods */}
                <div className="mt-5">
                  <p className="mb-3 text-xs font-semibold tracking-[0.1em] text-[#49526d] uppercase">
                    Método de pago
                  </p>
                  <div className="grid gap-3 sm:grid-cols-3">
                    {PAYMENT_METHODS.map(({ id, title, subtitle, time, cost, Icon }) => {
                      const selected = method === id;
                      return (
                        <button
                          key={id}
                          type="button"
                          onClick={() => setMethod(id)}
                          className={[
                            "relative rounded-2xl border p-4 text-left transition-all",
                            selected
                              ? "border-[#2563d9] bg-white ring-2 ring-[#2563d9]/20"
                              : "border-[#ddd4c8] bg-white hover:border-[#b5c5e8]",
                          ].join(" ")}
                        >
                          {selected && (
                            <span className="absolute top-3 right-3 flex h-5 w-5 items-center justify-center rounded-full bg-[#2563d9] text-[10px] font-bold text-white">
                              ✓
                            </span>
                          )}
                          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[#f0ebe4] text-[#39425d]">
                            <Icon className="h-4.5 w-4.5" />
                          </div>
                          <p className="mt-3 text-sm font-bold text-[#1f2740]">{title}</p>
                          <p className="mt-0.5 text-xs text-[#6a728a]">{subtitle}</p>
                          <div className="mt-3 flex items-center gap-1.5">
                            <span className="text-xs font-semibold text-[#39425d]">{time}</span>
                            <span className="text-xs text-[#9ba5bd]">·</span>
                            <span className="text-xs text-[#39425d]">{cost}</span>
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Detail form */}
                {method === "BANK_TRANSFER" && (
                  <BankTransferForm value={bankDetails} onChange={setBankDetails} />
                )}
                {method === "WISE" && (
                  <WiseForm value={wiseDetails} onChange={setWiseDetails} />
                )}
                {method === "USDC" && (
                  <UsdcForm value={usdcDetails} onChange={setUsdcDetails} />
                )}

                {/* Error banner */}
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
              onBack={() => navigate("/sign")}
              onNext={handleNext}
              nextDisabled={!isValid() || isSubmitting}
            />
          </div>

          <aside className="border-l border-[#ddd4c8] bg-[#f7f3ed] p-4">
            <StepsTrackingPanel
              userName={fullName}
              steps={[
                { number: 1, label: "Datos personales", done: true },
                { number: 2, label: "Documentos", done: true },
                { number: 3, label: "Firma de contrato", done: true },
                { number: 4, label: "Método de pago", active: true },
                { number: 5, label: "Verificación de identidad" },
              ]}
            />
          </aside>
        </div>
      </section>
    </main>
  );
}
