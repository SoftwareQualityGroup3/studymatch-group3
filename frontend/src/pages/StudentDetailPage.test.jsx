import { cleanup, render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import StudentDetailPage from './StudentDetailPage'
import { ApiError, getStudent } from '../api/students'

vi.mock('../api/students', async (importOriginal) => ({
  ...(await importOriginal()),
  getStudent: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

// a página lê o id do endereço, por isso precisa de estar dentro de uma rota com :id
function renderAt(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/students/:id" element={<StudentDetailPage />} />
      </Routes>
    </MemoryRouter>
  )
}

describe('StudentDetailPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('apresenta os dados do estudante e a ligação para editar', async () => {
    getStudent.mockResolvedValue({
      id: 4,
      number: '100',
      name: 'Ana Silva',
      email: 'ana@isep.ipp.pt',
      course: 'LEI',
      curricularYear: 2,
    })

    renderAt('/students/4')

    expect(screen.getByText('A carregar...')).toBeTruthy()
    expect(await screen.findByRole('heading', { name: 'Ana Silva' })).toBeTruthy()
    expect(getStudent).toHaveBeenCalledWith('4')
    expect(screen.getByText('ana@isep.ipp.pt')).toBeTruthy()
    expect(screen.getByText('LEI')).toBeTruthy()
    expect(screen.getByRole('link', { name: 'Editar' }).getAttribute('href')).toBe(
      '/students/4/edit'
    )
  })

  it('deixa espaço para as secções das próximas issues', async () => {
    getStudent.mockResolvedValue({ id: 4, number: '100', name: 'Ana', curricularYear: 1 })

    renderAt('/students/4')

    await screen.findByRole('heading', { name: 'Ana' })
    expect(screen.getByTestId('reserved-S2-06')).toBeTruthy()
    expect(screen.getByTestId('reserved-S2-09')).toBeTruthy()
    expect(screen.getByTestId('reserved-S2-11')).toBeTruthy()
  })

  it('mostra um traço nos campos opcionais vazios', async () => {
    getStudent.mockResolvedValue({
      id: 4,
      number: '100',
      name: 'Ana',
      email: null,
      course: null,
      curricularYear: 1,
    })

    renderAt('/students/4')

    await screen.findByRole('heading', { name: 'Ana' })
    expect(screen.getAllByText('—')).toHaveLength(2)
  })

  it('diz que o estudante não foi encontrado num 404', async () => {
    getStudent.mockRejectedValue(new ApiError(404, 'Estudante não encontrado.'))

    renderAt('/students/99')

    expect((await screen.findByRole('alert')).textContent).toBe('Estudante não encontrado.')
  })

  it('pede para verificar o backend quando o servidor está desligado', async () => {
    getStudent.mockRejectedValue(new ApiError(0, 'Não foi possível contactar o servidor.'))

    renderAt('/students/4')

    expect((await screen.findByRole('alert')).textContent).toContain('Verifique se o backend')
  })
})
