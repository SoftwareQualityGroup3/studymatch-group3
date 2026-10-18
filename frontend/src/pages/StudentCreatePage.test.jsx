import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import StudentCreatePage from './StudentCreatePage'
import { ApiError, createStudent } from '../api/students'

// mantém o erro da api verdadeiro e troca só a função que fala com o backend
vi.mock('../api/students', async (importOriginal) => ({
  ...(await importOriginal()),
  createStudent: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

// a rota do detalhe só mostra um texto, serve para confirmar que houve navegação
function renderPage() {
  return render(
    <MemoryRouter initialEntries={['/students/new']}>
      <Routes>
        <Route path="/students/new" element={<StudentCreatePage />} />
        <Route path="/students/:id" element={<p>detalhe aberto</p>} />
      </Routes>
    </MemoryRouter>
  )
}

// escreve numa caixa a partir da label dela
function fill(label, value) {
  fireEvent.change(screen.getByLabelText(label), { target: { value } })
}

function fillValidStudent() {
  fill('Número de estudante', '123')
  fill('Nome', 'Ana Silva')
  fill('Contacto institucional (email)', 'ana@isep.ipp.pt')
  fill('Curso', 'LEI')
  fill('Ano curricular', '2')
}

describe('StudentCreatePage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('envia os dados do formulário e abre o detalhe do estudante criado', async () => {
    createStudent.mockResolvedValue({ id: 7 })
    renderPage()

    fillValidStudent()
    fireEvent.click(screen.getByRole('button', { name: 'Criar estudante' }))

    expect(await screen.findByText('detalhe aberto')).toBeTruthy()
    // o ano curricular tem de seguir como número e não como texto
    expect(createStudent).toHaveBeenCalledWith({
      number: '123',
      name: 'Ana Silva',
      email: 'ana@isep.ipp.pt',
      course: 'LEI',
      curricularYear: 2,
    })
  })

  it('mostra o erro do número repetido (409) junto do campo', async () => {
    createStudent.mockRejectedValue(
      new ApiError(409, 'Já existe um estudante com este número.', {
        number: 'Student number 123 already exists',
      })
    )
    renderPage()

    fillValidStudent()
    fireEvent.click(screen.getByRole('button', { name: 'Criar estudante' }))

    expect(await screen.findByText('Student number 123 already exists')).toBeTruthy()
    expect(screen.getByRole('alert').textContent).toBe('Já existe um estudante com este número.')
    // continua no formulário para o utilizador poder corrigir
    expect(screen.queryByText('detalhe aberto')).toBeNull()
  })

  it('mostra os erros de validação (400) de cada campo', async () => {
    createStudent.mockRejectedValue(
      new ApiError(400, 'Os dados enviados não são válidos.', {
        name: 'name is required',
        curricularYear: 'curricularYear must be between 1 and 5',
      })
    )
    renderPage()

    fireEvent.click(screen.getByRole('button', { name: 'Criar estudante' }))

    expect(await screen.findByText('name is required')).toBeTruthy()
    expect(screen.getByText('curricularYear must be between 1 and 5')).toBeTruthy()
    // a caixa com erro fica ligada à mensagem
    expect(screen.getByLabelText('Nome').getAttribute('aria-invalid')).toBe('true')
  })

  it('manda o ano curricular vazio como null', async () => {
    createStudent.mockRejectedValue(new ApiError(400, 'inválido', {}))
    renderPage()

    fireEvent.click(screen.getByRole('button', { name: 'Criar estudante' }))

    await waitFor(() => expect(createStudent).toHaveBeenCalled())
    expect(createStudent.mock.calls[0][0].curricularYear).toBeNull()
  })

  it('mostra um erro quando o backend está desligado', async () => {
    createStudent.mockRejectedValue(new ApiError(0, 'Não foi possível contactar o servidor.'))
    renderPage()

    fillValidStudent()
    fireEvent.click(screen.getByRole('button', { name: 'Criar estudante' }))

    expect((await screen.findByRole('alert')).textContent).toBe(
      'Não foi possível contactar o servidor.'
    )
  })

  it('bloqueia o botão enquanto o pedido decorre', async () => {
    // promessa que só resolvemos nós, para o pedido ficar "a decorrer"
    let resolveRequest
    createStudent.mockReturnValue(new Promise((resolve) => (resolveRequest = resolve)))
    renderPage()

    fillValidStudent()
    fireEvent.click(screen.getByRole('button', { name: 'Criar estudante' }))

    const button = await screen.findByRole('button', { name: 'A guardar...' })
    expect(button.disabled).toBe(true)

    resolveRequest({ id: 7 })
    expect(await screen.findByText('detalhe aberto')).toBeTruthy()
  })
})
