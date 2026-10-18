// o vite faz proxy de /api para o backend (ver vite.config.js)
const STUDENTS_URL = '/api/students'

// erro da api com o estado http e os erros por campo que o backend envia
// ex: o campo number com a mensagem de número repetido
export class ApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

// mensagem para mostrar ao utilizador quando o backend não manda nenhuma geral
function defaultMessage(status) {
  switch (status) {
    case 400:
      return 'Os dados enviados não são válidos. Corrija os campos assinalados.'
    case 404:
      return 'Estudante não encontrado.'
    case 409:
      return 'Já existe um estudante com este número.'
    default:
      return `Erro ${status} ao contactar a api.`
  }
}

// lê o corpo da resposta como json e devolve null se não for json
// (ex: resposta vazia ou página de erro do proxy)
async function readBody(response) {
  try {
    return await response.json()
  } catch {
    return null
  }
}

// faz o pedido e converte qualquer falha num erro da api
async function request(url, options) {
  let response
  try {
    response = await fetch(url, options)
  } catch {
    // o fetch só falha assim quando nem chegou a haver resposta
    // (backend desligado, sem rede...), por isso o estado fica a 0
    throw new ApiError(0, 'Não foi possível contactar o servidor.')
  }

  const body = await readBody(response)

  if (!response.ok) {
    // o backend manda os erros num objeto com um texto por campo
    // e o erro "general" é uma regra de domínio que não é de nenhum campo
    const fieldErrors = body?.fieldErrors ?? {}
    const message = fieldErrors.general ?? defaultMessage(response.status)
    throw new ApiError(response.status, message, fieldErrors)
  }

  return body
}

// opções dos pedidos que enviam dados em json (post e put)
function jsonOptions(method, data) {
  return {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  }
}

// get /api/students
export function listStudents() {
  return request(STUDENTS_URL)
}

// get /api/students/{id}
export function getStudent(id) {
  return request(`${STUDENTS_URL}/${id}`)
}

// post /api/students (responde 201 com o estudante criado)
export function createStudent(data) {
  return request(STUDENTS_URL, jsonOptions('POST', data))
}

// put /api/students/{id}
export function updateStudent(id, data) {
  return request(`${STUDENTS_URL}/${id}`, jsonOptions('PUT', data))
}

// put /api/students/{id}/self-assessment (responde 200 com as notas guardadas)
export function saveSelfAssessment(id, data) {
  return request(`${STUDENTS_URL}/${id}/self-assessment`, jsonOptions('PUT', data))
}
