import { z } from "zod";

export const Step1Schema = z.object({
  name: z.string().min(3, "Minimo 3 caracteres"),
  lastName: z.string().min(3, "Minimo 3 caracteres"),
  nickName: z.string().min(3, "Minimo 3 caracteres"),
  birthDate: z
    .string()
    .min(1, "La fecha de nacimiento es requerida")
    .regex(/^(0[1-9]|[12]\d|3[01])\/(0[1-9]|1[0-2])\/(\d{4})$/, "El formato debe ser DD/MM/AAAA")
    .transform((dateString) => {
      const [day, month, year] = dateString.split("/");
      return new Date(`${year}-${month}-${day}`);
    })
    .refine((date) => {
      // Validar que la fecha sea válida
      return !isNaN(date.getTime());
    }, "La fecha no es válida")
    .refine((date) => date <= new Date(), "La fecha de nacimiento no puede ser en el futuro")
    .refine((date) => {
      const today = new Date();
      let age = today.getFullYear() - date.getFullYear();
      const monthDiff = today.getMonth() - date.getMonth();
      const dayDiff = today.getDate() - date.getDate();

      if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) {
        age--;
      }
      return age >= 18;
    }, "Debe ser mayor de 18 años"),
});
