# 🧪 Plan de Pruebas y Estrategia de Validación: Ecosistema NorthPay

## 1. Alcance y Estrategia

El objetivo de este plan es validar el comportamiento funcional, la integridad de las API y el control de acceso del MVP de NorthPay. Las pruebas se ejecutan de manera automatizada utilizando **Postman** sobre el entorno de desarrollo backend hospedado en **Hugging Face Spaces**.

## 2. Criterios de Aceptación y Calidad

* **Códigos de Estado HTTP:** Validación de respuestas esperadas (`200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`).
* **Seguridad y Autenticación:** Consistencia en el envío y manejo de cabeceras de autorización (`Bearer Tokens`).
* **Persistencia de Datos:** Confirmación de almacenamiento y actualización correcta de los datos en **Supabase**.

## 3. Matriz de Cobertura (Módulos y Casos)

Para ver el detalle paso a paso de los casos de prueba ejecutables, consulta el archivo [`Casos_de_Prueba.md`](./Casos_de_Prueba.md).