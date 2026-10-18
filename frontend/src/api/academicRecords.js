const COURSE_UNITS_URL = '/api/course-units'

function recordsUrl(studentId) {
  return `/api/students/${studentId}/records`
}

// Erro próprio para guardar o status HTTP e erros dos campos.
export class AcademicRecordApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message)
    this.name = 'AcademicRecordApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

function defaultMessage(status) {
  switch (status) {
    case 400:
      return 'Os dados do resultado não são válidos.'
    case 404:
      return 'Estudante ou unidade curricular não encontrado.'
    case 409:
      return 'Já existe um resultado para esta unidade curricular.'
    default:
      return `Erro ${status} ao contactar a API.`
  }
}

// Tenta ler a resposta como JSON.
async function readBody(response) {
  try {
    return await response.json()
  } catch {
    return null
  }
}

// Função comum para fazer pedidos e tratar erros da API.
async function request(url, options) {
  let response

  try {
    response = await fetch(url, options)
  } catch {
    throw new AcademicRecordApiError(
      0,
      'Não foi possível contactar o servidor.'
    )
  }

  const body = await readBody(response)

  if (!response.ok) {
    const fieldErrors = body?.fieldErrors ?? {}

    const message =
      fieldErrors.general ??
      fieldErrors.courseUnitCode ??
      defaultMessage(response.status)

    throw new AcademicRecordApiError(
      response.status,
      message,
      fieldErrors
    )
  }

  return body
}

// Vai buscar as unidades curriculares.
export function listCourseUnits() {
  return request(COURSE_UNITS_URL)
}

// Vai buscar os resultados académicos do estudante.
export function listAcademicRecords(studentId) {
  return request(recordsUrl(studentId))
}

// Regista um novo resultado académico.
export function createAcademicRecord(studentId, data) {
  return request(recordsUrl(studentId), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
}
