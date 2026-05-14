import { useState } from "react";

import PersonalData from "./steps/Step1PersonalData";
import Documents from "./steps/Step2Documents";
import Sign from "./steps/Step3Sign";
import Payment from "./steps/Step4Payment";
import IdVerification from "./steps/Step5IdVerification";

const STEPS = [
  { title: "Personal-data", component: PersonalData },
  { title: "Documents", component: Documents },
  { title: "Sign", component: Sign },
  { title: "Payment", component: Payment },
  { title: "IdVerification", component: IdVerification },
];

export const Wizard = () => {
  const [current, setCurrent] = useState(0);
  const [loading, setLoading] = useState(false);

  const isLast = current === STEPS.length - 1;

  const StepComponent = STEPS[current].component;

  return (
    <div>
      <h1>Wizard</h1>
    </div>
  );
};
