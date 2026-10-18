import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError, createStudent, getStudent, listStudents, updateStudent } from './students'

// cria uma resposta falsa do fetch com o estado e o corpo que quisermos
function mockResponse(status, body) {
  return {
    ok: status >= 200 && status < 300,
    status,
    json: async () => {
      if (body === undefined) throw new Error('sem corpo')
      return body
    },
  }
}

// apanha o erro lançado por uma chamada à api para o podermos inspecionar
async function catchError(promise) {
  try {
    await promise
  } catch (error) {
    return error
  }
  throw new Error('era esperado um erro e não houve nenhum')
}

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('api de estudantes', () => {
  it('lista os estudantes com get /api/students', async () => {
    const fetchMock = vi.fn().mockResolvedValue(mockResponse(200, [{ id: 1 }]))
    vi.stubGlobal('fetch', fetchMock)

    const students = await listStudents()

    expect(fetchMock).toHaveBeenCalledWith('/api/students', undefined)
    expect(students).toEqual([{ id: 1 }])
  })

  it('obtém um estudante pelo id', async () => {
    const fetchMock = vi.fn().mockResolvedValue(mockResponse(200, { id: 4 }))
    vi.stubGlobal('fetch', fetchMock)

    await getStudent(4)

    expect(fetchMock).toHaveBeenCalledWith('/api/students/4', undefined)
  })

  it('cria um estudante com post e corpo em json', async () => {
    const fetchMock = vi.fn().mockResolvedValue(mockResponse(201, { id: 9 }))
    vi.stubGlobal('fetch', fetchMock)
    const data = { number: '123', name: 'Ana' }

    const created = await createStudent(data)

    const [url, options] = fetchMock.mock.calls[0]
    expect(url).toBe('/api/students')
    expect(options.method).toBe('POST')
    expect(JSON.parse(options.body)).toEqual(data)
    expect(created).toEqual({ id: 9 })
  })

  it('atualiza um estudante com put no id certo', async () => {
    const fetchMock = vi.fn().mockResolvedValue(mockResponse(200, { id: 4 }))
    vi.stubGlobal('fetch', fetchMock)

    await updateStudent(4, { name: 'Ana' })

    const [url, options] = fetchMock.mock.calls[0]
    expect(url).toBe('/api/students/4')
    expect(options.method).toBe('PUT')
  })

  it('devolve os erros por campo num 400', async () => {
    const fieldErrors = { name: 'name is required' }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(400, { fieldErrors })))

    const error = await catchError(createStudent({}))

    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(400)
    expect(error.fieldErrors).toEqual(fieldErrors)
  })

  it('usa o erro general do backend como mensagem', async () => {
    const body = { fieldErrors: { general: 'Grade must be between 0 and 20' } }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(400, body)))

    const error = await catchError(createStudent({}))

    expect(error.message).toBe('Grade must be between 0 and 20')
  })

  it('devolve o erro do número repetido num 409', async () => {
    const fieldErrors = { number: 'Student number 123 already exists' }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(409, { fieldErrors })))

    const error = await catchError(createStudent({}))

    expect(error.status).toBe(409)
    expect(error.fieldErrors.number).toBe('Student number 123 already exists')
  })

  it('devolve o estado 404 quando o estudante não existe', async () => {
    const body = { fieldErrors: { id: 'Student with id 99 not found' } }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(404, body)))

    const error = await catchError(getStudent(99))

    expect(error.status).toBe(404)
  })

  it('aguenta uma resposta de erro sem corpo json', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(500, undefined)))

    const error = await catchError(listStudents())

    expect(error.status).toBe(500)
    expect(error.fieldErrors).toEqual({})
  })

  it('devolve o estado 0 quando o backend está desligado', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('falhou')))

    const error = await catchError(listStudents())

    expect(error.status).toBe(0)
    expect(error.message).toBe('Não foi possível contactar o servidor.')
  })
})
