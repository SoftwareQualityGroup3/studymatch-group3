const STUDENTS_URL = "/api/students";

// erro personalizado para guardar a mensagem, o estado HTTP e os erros dos campos
export class ApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

// obtém os erros dos campos enviados pelo backend
function normalizeFieldErrors(body) {
  return body?.fieldErrors ?? {};
}

// lê o corpo da resposta como json e se não conseguir devolve null
async function readBody(response) {
  try {
    return await response.json();
  } catch {
    return null;
  }
}

// envia pedidos à api e trata os erros
async function request(url, options) {
  let response;
  try {
    // faz pedido ao servidor
    response = await fetch(url, options);
  } catch {
    // o pedido falhou antes de receber uma resposta do servidor
    throw new ApiError(0, "Não foi possível contactar o servidor.");
  }

  // lê a resposta mesmo quando o pedido falha para obter a mensagem de erro
  const body = await readBody(response);

  if (!response.ok) {
    // usa a mensagem do backend ou se não existir uma mensagem genérica
    const message =
      body?.message ??
      body?.detail ??
      `Erro ${response.status} ao contactar a API.`;
    throw new ApiError(response.status, message, normalizeFieldErrors(body));
  }

  return body;
}

// prepara as opções dos pedidos que enviam dados em json
function jsonOptions(method, data) {
  return {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data), // converte os dados javascript em texto json
  };
}

export function listStudents() {
  return request(STUDENTS_URL);
}

export function getStudent(id) {
  return request(`${STUDENTS_URL}/${id}`);
}

export function createStudent(data) {
  return request(STUDENTS_URL, jsonOptions("POST", data));
}

export function updateStudent(id, data) {
  return request(`${STUDENTS_URL}/${id}`, jsonOptions("PUT", data));
}
