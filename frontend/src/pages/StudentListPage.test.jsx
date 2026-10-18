import { cleanup, render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import StudentListPage from './StudentListPage'
import { listStudents } from '../api/students'

vi.mock('../api/students', () => ({
  listStudents: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

function renderPage() {
  return render(
    <MemoryRouter>
      <StudentListPage />
    </MemoryRouter>
  )
}

describe('StudentListPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('apresenta os estudantes numa tabela com ligação para o detalhe', async () => {
    listStudents.mockResolvedValue([
      { id: 1, number: '100', name: 'Ana', email: null, course: 'LEI', curricularYear: 2 },
    ])

    renderPage()

    expect(screen.getByText('A carregar...')).toBeTruthy()
    const link = await screen.findByRole('link', { name: 'Ana' })
    expect(link.getAttribute('href')).toBe('/students/1')
    expect(screen.getByText('100')).toBeTruthy()
    expect(screen.getByText('LEI')).toBeTruthy()
  })

  it('mostra um traço quando o curso não está preenchido', async () => {
    listStudents.mockResolvedValue([
      { id: 1, number: '100', name: 'Ana', email: null, course: null, curricularYear: 1 },
    ])

    renderPage()

    expect(await screen.findByText('—')).toBeTruthy()
  })

  it('avisa quando ainda não há estudantes', async () => {
    listStudents.mockResolvedValue([])

    renderPage()

    expect(await screen.findByText('Ainda não existem estudantes registados.')).toBeTruthy()
  })

  it('apresenta um erro quando o backend está indisponível', async () => {
    listStudents.mockRejectedValue(new Error('sem backend'))

    renderPage()

    expect((await screen.findByRole('alert')).textContent).toContain(
      'Não foi possível carregar os estudantes'
    )
  })

  it('tem uma ligação para criar um estudante', () => {
    listStudents.mockResolvedValue([])

    renderPage()

    const link = screen.getByRole('link', { name: 'Novo estudante' })
    expect(link.getAttribute('href')).toBe('/students/new')
  })
})
