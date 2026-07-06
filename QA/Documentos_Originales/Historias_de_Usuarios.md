HISTORIAS DE USUARIOS Y CRITERIOS DE ACEPTACION
Épica 1: Gestión de Invitaciones y Acceso Seguro (Rol A)
HU #01: Acceso al portal mediante Token Seguro
 Descripción: Como contratista autorizado, quiero recibir un correo con un token único y temporal para acceder al portal de onboarding de forma segura sin usar contraseñas tradicionales.
 Criterios de Aceptación (CA):
1. El sistema debe generar un token único UUID y que el envío sea estrictamente vía Brevo SMTP.
2. El token debe tener una validez estricta de 24 horas; después de ese tiempo, el acceso debe denegarse.
3. Al hacer clic en el link, el sistema debe validar el token único para permitir el acceso al proceso de onboarding.
4. El administrador (operador) no utiliza tokens, debe acceder mediante un Login tradicional (Email/Password con BCrypt).
Épica 2: Onboarding Autogestionado y Carga de Documentos (Rol B)
HU #02: Carga de Datos Personales y Documentación
 Descripción: Como contratista, quiero completar mis datos personales y subir mis documentos de identidad (DNI/Pasaporte) para que mi perfil pueda ser validado por la administración.
 Criterios de Aceptación (CA):
1. El formulario debe capturar campos obligatorios: Nombre, Apellido, Email y País.
2. El módulo de archivos debe permitir formatos PDF, JPG y PNG.
3. Los archivos deben almacenarse de forma segura en Supabase Storage.
4. El sistema debe rechazar los archivos que superen los 6 MB.
5. El usuario no puede avanzar a la siguiente sección sin completar todos los campos obligatorios.
6. La carga de documentos debe disparar un cambio de estado a DOCUMENTS_UPLOADED y se debe notificar al operador.
HU #03: Firma de Contrato y Configuración de Pagos
 Descripción: Como contratista, quiero visualizar y firmar mi contrato digitalmente y configurar mis métodos de pago para formalizar mi relación con la empresa.
 Criterios de Aceptación (CA):
1. El portal debe mostrar el contrato legal generado dinámicamente con los datos del usuario.
2. El usuario debe poder configurar sus pagos, y el sistema debe soportar múltiples variantes (SWIFT, ACH, Cripto) almacenando los datos en formato JSONB.
3. El sistema debe registrar la firma digital y cambiar el estado del onboarding a "Pending Verification".
4. El contrato firmado debe almacenarse automáticamente en Supabase Storage, vinculando la URL generada en la tabla documents.
Épica 3: Panel de Operaciones y Verificación (Rol C)
HU #04: Panel de Gestión y Priorización (Operador)
 Descripción: Como operador administrativo, quiero visualizar el listado de contratistas con indicadores de urgencia para priorizar las revisiones según el tiempo de espera (SLA).
 Criterios de Aceptación (CA):
1. El panel debe permitir filtrar contratistas por país y por estado (Pendiente, Aprobado, Rechazado).
2. Nuevo: El listado debe incluir el campo hours_since_update que determine un coloreado visual (SLA) para identificar rápidamente qué solicitudes llevan más tiempo sin atención.
3. Cada acción realizada en el panel debe quedar registrada en el log de auditoría del sistema.
HU #05: Validación y Lógica de Rechazo en Cascada
 Descripción: Como operador administrativo, quiero poder aprobar o solicitar correcciones en la documentación, asegurando que cualquier rechazo invalide los pasos posteriores para mantener la consistencia de los datos.
 Criterios de Aceptación (CA):
1. El operador debe tener botones claramente definidos para "Aprobar" y "Solicitar Corrección".
2. Si el operador solicita una corrección en el Paso 2 (Documentación), el sistema debe aplicar una lógica de cascada e invalidar automáticamente el contrato del Paso 3.
3. El sistema no debe permitir la activación final del contratista si existen pasos previos marcados para corrección.
HU #06: Centro de Feedback del Operador (HU #09 doc. anterior)
 Descripción: Como contratista, quiero ver los comentarios específicos del operador para corregir mis documentos sin adivinar el error.
 Criterios de Aceptación (CA):
1. El sistema debe exponer un endpoint GET /api/onboarding/{id}/comments que devuelva el historial completo de observaciones realizadas por el operador.
2. La interfaz del contratista debe mostrar claramente el mensaje de error o sugerencia vinculado a cada documento rechazado. Los comentarios deben actualizarse en tiempo real para que el contratista pueda realizar las correcciones de inmediato
Épica 4: Infraestructura, Base de Datos y Motor de Estados (Core)
HU #07: Notificaciones Automáticas de Estado
 Descripción: Como usuario del sistema (contractor o admin), quiero recibir correos automáticos cuando mi estado cambie para estar informado sobre el progreso del proceso.
 Criterios de Aceptación (CA):
1. El sistema debe enviar un correo vía Brevo SMTP cuando el administrador apruebe o rechace un documento.
2. Las transiciones de estado (ej: de "Pending" a "Activated") deben ser automáticas tras la validación final.