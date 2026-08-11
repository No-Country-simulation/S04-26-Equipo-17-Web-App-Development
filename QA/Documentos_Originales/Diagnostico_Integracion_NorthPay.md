# INFORME DE DIAGNÓSTICO TÉCNICO: INTEGRACIÓN FRONTEND-BACKEND
**Proyecto:** NorthPay Portal  
**Área:** Quality Assurance (QA)  
**Fecha:** 29 de mayo de 2026  
**Asunto:** Análisis de brechas en la integración y optimización de endpoints.

## 1. INTRODUCCIÓN Y ALCANCE
El presente informe técnico detalla los hallazgos tras la validación de la integración entre el sistema de diseño (UI) y las capacidades lógicas actuales del backend (Swagger). El objetivo es asegurar la escalabilidad y la eficiencia en la comunicación entre capas antes de la fase de despliegue.

## 2. HALLAZGOS TÉCNICOS GENERALES
*   **Confusión de Módulos (Login):** El desarrollo tomó como referencia el endpoint `POST /api/auth/login`, el cual pertenece exclusivamente al módulo interno de Operadores (Administración) con datos de prueba preconfigurados. Esto no valida ni resuelve el flujo real del Portal de Contratistas, el cual es un módulo externo totalmente independiente.
*   **Inconsistencia Crítica de Flujo:** El orden lógico planteado en el sistema de diseño (5 pasos sucesivos) está roto en el backend. Se están exigiendo datos avanzados (como firmas y validaciones de identidad) en las fases iniciales del registro, lo que impide que el usuario pueda avanzar secuencialmente.
*   **Desalineación Frontend-Backend:** Las pantallas del portal solicitan datos adaptados a la experiencia del usuario (según el país), mientras que los endpoints del Swagger esperan variables con estructuras diferentes o rígidas, lo que provocará fallos en la integración del código (`400 Bad Request`).

---

## 3. ANÁLISIS DETALLADO POR PANTALLA (PASO A PASO)

### Paso 1: Interfaz de Datos Personales
![Paso 1](../../../Imagenes/Interfaz_de_datos_personales_del_contratista.png)
*   **El Hallazgo (Discrepancia Técnica):** Al contrastar la interfaz del Paso 1 con el Swagger, se observa una falta de correlación entre los campos requeridos por el backend y los campos de entrada de datos diseñados en la pantalla del usuario. El backend exige parámetros obligatorios específicos que la interfaz gráfica no recolecta ni contempla en su diseño.
*   **El Impacto:** Error en la integración de datos. Al intentar procesar el formulario, la comunicación entre el frontend y el backend fallará devolviendo un error `400 Bad Request`, ya que el cliente no enviará las variables obligatorias que el servidor espera recibir.
*   **La Solución:** Estandarizar la estructura del endpoint del Paso 1 en Swagger para que refleje de manera exacta los mismos campos de información que el usuario completa en la interfaz gráfica del portal (Nombre legal, Apellidos, Cómo te llamamos, Fecha de nacimiento, País de residencia, Cédula y datos de contacto).

### Paso 2: Carga de Documentos (POST /api/onboarding/documents)
*   **Parámetros Obligatorios Cruzados:** El endpoint marca como obligatorios (`required: true`) los campos `CONTRATO_FIRMADO` y `SELFIE`. Según el diseño visual, el contrato corresponde al Paso 3 y la selfie al Paso 5. Exigirlos de forma anticipada en el Paso 2 (Documentos) bloquea por completo el avance del registro.
*   **Omisión de Campos del Diseño:** La interfaz gráfica para Colombia detalla la opción de adjuntar un "Extracto bancario (Opcional)". Sin embargo, en la estructura técnica de este endpoint no existe ningún campo destinado a recibir dicho archivo.
*   **Error de Nomenclatura (Typo):** La variable para el número de identificación está escrita en el backend como `Numero_ficial` (omitiendo la letra "O" inicial). Esta desviación generará fallos de coincidencia (*mismatch*) cuando el frontend intente enviar el dato correctamente formateado como `Numero_oficial`.

### Paso 3: Firma de Contrato (POST /api/onboarding/{id}/contract)
*   **Cruce de Parámetros (Bloqueante):** El endpoint exige obligatoriamente un parámetro de camino (*path parameter*) denominado `id` (identificación del onboarding) en la URL. Sin embargo, en la interfaz del Paso 3 no existe ningún campo para que el usuario ingrese este dato de forma manual. El frontend queda "a ciegas" al no tener documentado cómo recuperar o heredar ese ID internamente desde los pasos anteriores, bloqueando el caso de prueba de renderizado del PDF.
*   **Contrato de API Incompleto:** El Swagger solo pide el ID en la URL, pero no tiene ninguna variable en el cuerpo de la petición (`Request Body`) para recibir los datos de la firma manuscrita que el usuario dibuja en la pantalla. Sin este campo, es imposible procesar el contrato y cambiar el estado a firmado (`CONTRACT_SIGNED`).
*   **La Solución:** El backend debe corregir el Swagger eliminando la exigencia manual del ID (debe heredarse de forma transparente y automatizada por el frontend) y agregar el campo correspondiente para recibir los datos de la firma digitalizada.

### Paso 4: Método de Pago (POST /api/onboarding/{id}/step4/payment)
*   **Diagnóstico del Componente Técnico:** Inexistencia de Endpoint de Consulta (Vacío de Backend).
*   **Evidencia del Hallazgo:** La pantalla de progreso y activación requiere consumir un método de lectura (`GET`) para poder mostrar de forma dinámica los estados aprobados, el enmascaramiento de la cuenta bancaria, la conversión de divisas y el porcentaje de coincidencia biométrica. El Swagger analizado solo provee un método de inserción (`POST`), el cual pertenece al formulario de carga de datos y no a la visualización final.
*   **Impacto en el Proyecto:** El desarrollo de esta interfaz se encuentra bloqueado. El frontend no dispone de ninguna API existente en el Swagger actual de donde extraer la información consolidada para pintar la pantalla de éxito.
*   **La Solución:** Se solicita al equipo de backend la creación y documentación de un nuevo endpoint de tipo consulta (`GET /api/onboarding/{id}/activation-summary`) que devuelva el objeto JSON con todos los datos procesados.

### Paso 5: Validación de Identidad (POST /api/onboarding/{id}/step5/selfie)
*   **Soporte de API Correcto para Captura Única:** El endpoint respalda adecuadamente la acción de enviar la imagen del rostro del usuario hacia el servidor para disparar el proceso de comparación biométrica.
*   **Ambigüedad en el Control de Estados ("Verificando"):** Mientras que las comprobaciones de "Autenticidad del documento" y "Coincidencia facial" figuran como *Aprobado*, el ítem "Detección de vida" se muestra en estado *Verificando*. El endpoint inicia la transición del flujo al estado `PENDING_VERIFICATION`. Sin embargo, la documentación de la API no especifica si este método devuelve de forma inmediata el resultado de la prueba de vida o si el frontend requiere implementar un mecanismo de consulta continua (*polling*) para actualizar el botón "Enviar para revisión".
*   **La Solución:** Se recomienda al equipo de desarrollo asegurar que la respuesta del endpoint de la selfie incluya los códigos de estado intermedios para la "Detección de vida", garantizando que la interfaz pueda alternar dinámicamente entre el estado "Verificando" y el estado final de éxito antes de permitir el envío definitivo del registro.

---

## 4. CONCLUSIÓN GENERAL Y REQUERIMIENTOS DE BLOQUEO

### Interfaz: "¡Buen trabajo, Sofi! Listo para revisión"
*   **Diagnóstico de QA:** **Missing Endpoint (Vacío de Desarrollo).** No existe ningún endpoint en toda la plataforma actual que respalde esta interfaz de resumen. El programador estructuró las APIs para recibir y guardar la información (`POST` y `PUT`), pero omitió por completo desarrollar el endpoint de lectura final que consolide y entregue esos datos de vuelta al frontend para construir la vista de éxito.
*   **Solución Requerida:** Exponer un nuevo endpoint estructurado bajo el patrón: `GET /api/onboarding/{id}/summary`. Este método deberá mapear la base de datos y retornar un objeto JSON con los estados aprobados de cada fase.

### Interfaz: Dashboard Principal de Contratista Activo
*   **Fragmentación de APIs:** Para renderizar esta pantalla única, el frontend se ve obligado a realizar múltiples peticiones HTTP individuales (`GET` a la línea de tiempo, `GET` al preview del contrato, etc.). Esto incrementa notablemente la latencia y la carga en el cliente.
*   **Falta de Endpoint de Perfil Avanzado:** El endpoint existente `GET /api/auth/me` solo devuelve datos básicos de sesión (ID, email, rol). No incluye campos de negocio avanzados como la fecha del próximo pago calculada (5 de junio) o el equivalente en moneda local (COP 21.304.000).
*   **Solución Requerida:** Solicitar la creación de un endpoint unificado de dashboard: `GET /api/contractor/dashboard-summary`, que entregue de manera directa al frontend los estados de progreso consolidados, el monto mensualizado y las fechas de pago calculadas por el sistema.