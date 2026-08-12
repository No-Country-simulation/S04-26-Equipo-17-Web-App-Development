# Estructura de Épicas y Backlog de Alto Nivel: NorthPay MVP

## Épica 1: Gestión de Invitaciones y Acceso Seguro (Rol A)
* **Objetivo:** Validar el punto de entrada al sistema y la comunicación externa.
* **Alcance Técnico:** Generación de tokens únicos (UUID), validación de expiración de 24 horas, integración con Brevo SMTP.
* **Paquetes asociados:** `invitation/`, `notification/`.
* **Valor QA:** Asegura que el acceso sea controlado y que las alertas lleguen al usuario.

## Épica 2: Onboarding Autogestionado y Carga de Documentos (Rol B)
* **Objetivo:** Garantizar que el contratista complete los 5 pasos sin saltarse la lógica de negocio.
* **Alcance Técnico:** Máquina de estados (`StateMachineService`), subida de archivos a Supabase Storage (`Identity`, `Tax ID`), generación de PDF y configuración de pagos (`JSONB`).
* **Paquetes asociados:** `onboarding/`, `document/`, `payment/`.
* **Valor QA:** Verifica que el "Estado de Onboarding" progrese correctamente según la Tabla 1 del plan.

## Épica 3: Panel de Operaciones y Verificación (Rol C)
* **Objetivo:** Auditar las herramientas de gestión del operador y la respuesta del sistema.
* **Alcance Técnico:** Dashboard operativo con filtros (SLA, país), autenticación simple (BCrypt), integración con Pusher para actualizaciones instantáneas y flujo de activación/rechazo final y centro de feedback.
* **Paquetes asociados:** `operations/`.
* **Valor QA:** Valida la eficiencia del operador y que los cambios se vean sin recargar la página.

## Épica 4: Infraestructura, Base de Datos y Motor de Estados (Core)
* **Objetivo:** Evaluar la estabilidad de la base de datos y la consistencia de los datos.
* **Alcance Técnico:** Configuración de Supabase (PostgreSQL), ejecución de scripts Flyway (V1 y V2) y manejo global de excepciones.
* **Paquetes asociados:** `common/`.
* **Valor QA:** Garantiza que los datos se guarden correctamente y que el sistema no falle ante errores inesperados.