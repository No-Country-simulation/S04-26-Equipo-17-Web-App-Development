P# 🧪 Plan de Pruebas y Protocolo de Validación: Ecosistema NorthPay

## 1. Alcance y Estrategia (Plan de Pruebas)
El objetivo de este plan es validar el comportamiento funcional, la integridad de las APIs y el control de acceso del MVP de NorthPay. Las pruebas se ejecutan de manera automatizada utilizando **Postman** sobre el entorno de desarrollo backend en Hugging Face Spaces.

* **Criterios de Aceptación:** Validación de códigos de estado HTTP (200 OK, 401 Unauthorized), consistencia en las cabeceras Bearer y persistencia de datos en Supabase.
 
--Protocolo de Validación y Casos de Prueba: Ecosistema NorthPay v1.0 (Userstories backend.docx)
1. Módulo de Acceso e Inicio (Pre-Onboarding)
HU-01: Generación de Invitaciones
Descripcion: Como operador de NorthPay quiero registrar el correo de un nuevo contratista para enviarle un link de acceso único para iniciar el proceso de forma centralizada y controlada.
CP-01: Validar generación de token único.
2. Módulo del Contratista (Flujo de Registro)
HU-02: Carga de Perfil y Documentación (Pasos 1 y 2) Descripcion: Como contratista quiero completar mis datos personales y subir mis documentos de identidad para avanzar en mi proceso de validación.
CP-02: Verificar persistencia de datos personales.
CP-03: Evaluar subida de documentos de identidad.
HU-03: Formalización Legal y Firma (Paso 3) Descripcion: Como contratista quiero firmar digitalmente mi contrato de servicios para formalizar mi relación legal con NorthPay. CP-04 Comprobar generación de PDF de contrato.
CP-05: Validar cambio de estado tras firma.
HU-04: Métodos de Pago y Verificación (Pasos 4 y 5) Descripcion: Como contratista quiero configurar mi cuenta bancaria/crypto y realizar la selfie de identidad para finalizar mi carga de información.
CP-06: Verificar almacenamiento de pagos (JSONB).
HU-09: Centro de Feedback Interno (Eliminar WhatsApp) Descripcion: Como contratista quiero leer los comentarios de corrección directamente en el portal para resolver errores sin usar canales externos. CP-011: Verificar lectura de feedback interno.
3. Módulo de Operaciones y Control
HU-05: Panel de Visibilidad Operativa (SLA) Descripcion: Como operador quiero ver un dashboard con el estado y tiempo de cada onboarding para identificar procesos que excedan los 3 días de activación. CP-07: Evaluar resaltado visual de SLA.
HU-06: Gestión de Correcciones (Flujo de Cascada) Descripcion: Como operador quiero rechazar pasos específicos con comentarios técnicos para asegurar que la información sea correcta antes de aprobar CP-08: Comprobar flujo de cascada por rechazo.
HU-07: Activación Final de Cuenta Descripcion: Como operador quiero dar la aprobación definitiva al expediente completo para activar la cuenta y permitir que el contratista reciba pagos. CP-09: Validar activación definitiva de cuenta.
4.Módulo de Seguridad y Auditoría (Anexo)
HU-08: TRAZABILIDAD Y AUDITORIA (HISTORIAL) Eliminada Descripcion: Como operador quiero ver el historial de eventos de cada proceso para auditar quien aprobó de cada paso y entender retrasos.
CP-010: Auditar trazabilidad de eventos.
