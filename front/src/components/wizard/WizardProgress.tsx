"use client";

import { ArrowLeftIcon } from "lucide-react";
import { useState } from "react";

import {
  Stepper,
  StepperContent,
  StepperIndicator,
  StepperItem,
  StepperNav,
  StepperPanel,
  StepperTrigger,
} from "@/components/reui/stepper";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

const steps = [1, 2, 3, 4, 5];

export function WizardProgress() {
  const [currentStep, setCurrentStep] = useState(1);

  return (
    <div className="w-full max-w-md p-4">
      <Stepper value={currentStep} onValueChange={setCurrentStep}>
        <div className="flex items-center justify-start gap-2.5 py-1">
          <div className="text-sm font-medium">
            <span className="text-foreground">Paso {currentStep}</span>{" "}
            <span className="text-muted-foreground/60">de {steps.length}</span>
          </div>
        </div>
        <StepperNav>
          {steps.map((step) => (
            <StepperItem
              key={step}
              step={step}
              className="flex-1 overflow-hidden transition-all duration-300 first:rounded-s-full last:rounded-e-full"
            >
              <StepperTrigger className="w-full flex-col items-start gap-2" asChild>
                <StepperIndicator className="bg-border h-2 w-full rounded-none!">
                  <span className="sr-only">{step}</span>
                </StepperIndicator>
              </StepperTrigger>
            </StepperItem>
          ))}
        </StepperNav>

        <StepperPanel className="py-6 text-sm">
          {steps.map((step) => (
            <StepperContent
              className="flex w-full items-center justify-center"
              key={step}
              value={step}
            >
              Step {step} content
            </StepperContent>
          ))}
        </StepperPanel>

        <div className="flex items-center justify-between gap-2.5">
          <Button
            variant="link"
            onClick={() => setCurrentStep((prev) => prev - 1)}
            className={cn("px-0", currentStep === 1 && "pointer-events-none opacity-0")}
          >
            <ArrowLeftIcon className="size-4" />
            Back
          </Button>
          <Button
            variant="outline"
            onClick={() => setCurrentStep((prev) => prev + 1)}
            disabled={currentStep === steps.length}
          >
            Next
          </Button>
        </div>
      </Stepper>
    </div>
  );
}
