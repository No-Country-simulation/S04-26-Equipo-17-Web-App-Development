#  Protocolo de Validación y Casos de Prueba: Ecosistema NorthPay v1.0

## Módulo 1: Acceso e Inicio (Pre-Onboarding)
* **HU-01: Generación de Invitaciones**
  * **Descripción:** Como operador de NorthPay quiero registrar el correo de un nuevo contratista para enviarle un enlace de acceso único para iniciar el proceso de forma centralizada y controlada.
  * **CP-01:** Validar generación de token único.

---

## Módulo 2: Del Contratista (Flujo de Registro)
* **HU-02: Carga de Perfil y Documentación (Pasos 1 y 2)**
  * **Descripción:** Como contratista quiero completar mis datos personales y subir mis documentos de identidad para avanzar en mi proceso de validación.
  * **CP-02:** Verificar persistencia de datos personales.
  * **CP-03:** Evaluar subida de documentos de identidad.

* **HU-03: Formalización Legal y Firma (Paso 3)**
  * **Descripción:** Como contratista quiero firmar digitalmente mi contrato de servicios para formalizar mi relación legal con NorthPay.
  * **CP-04:** Comprobar generación de PDF de contrato.
  * **CP-05:** Validar cambio de estado tras firma.

* **HU-04: Métodos de Pago y Verificación (Pasos 4 y 5)**
  * **Descripción:** Como contratista quiero configurar mi cuenta bancaria/crypto y realizar la selfie de identidad para finalizar mi carga de información.
  * **CP-06:** Verificar almacenamiento de pagos (`JSONB`).

* **HU-09: Centro de Feedback Interno (Eliminar WhatsApp)**
  * **Descripción:** Como contratista quiero leer los comentarios de corrección directamente en el portal para resolver errores sin usar canales externos.
  * **CP-011:** Verificar lectura de retroalimentación interna.

---

## Módulo 3: De Operaciones y Control
* **HU-05: Panel de Visibilidad Operativa (SLA)**
  * **Descripción:** Como operador quiero ver un tablero con el estado y tiempo de cada onboarding para identificar procesos que exceden los 3 días de activación.
  * **CP-07:** Evaluación del resaltado visual de SLA.

* **HU-06: Gestión de Correcciones (Flujo de Cascada)**
  * **Descripción:** Como operador quiero rechazar pasos específicos con comentarios técnicos para asegurar que la información sea correcta antes de aprobar.
  * **CP-08:** Comprobar flujo de cascada por rechazo.

* **HU-07: Activación Final de Cuenta**
  * **Descripción:** Como operador quiero dar la aprobación definitiva al expediente completo para activar la cuenta y permitir que el contratista reciba pagos.
  * **CP-09:** Validar activación definitiva de cuenta.

---

## Módulo 4: Seguridad y Auditoría (Anexo)
* **HU-08: Trazabilidad y Auditoría (Historial) - [Eliminada del alcance MVP]**
  * **Descripción:** Como operador quiero ver el historial de eventos de cada proceso para auditar quién aprobó cada paso y entender retrasos.
  * **CP-010:** Auditoría de trazabilidad de eventos.
