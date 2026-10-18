import { cleanup, render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'
import { getHealth } from './api/health'
import { listStudents } from './api/students'

// as páginas chamam a api, por isso trocamos as funções por versões falsas
vi.mock('./api/health', () => ({
  getHealth: vi.fn(),
}))

vi.mock('./api/students', () => ({
  listStudents: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

// o app precisa de um router à volta, por isso usamos o memoryrouter
// que deixa escolher o endereço em que o teste começa
function renderAt(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <App />
    </MemoryRouter>
  )
}

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    listStudents.mockResolvedValue([])
    getHealth.mockResolvedValue({ status: 'UP', database: 'UP' })
  })

  it('apresenta o nome StudyMatch', () => {
    renderAt('/students')

    expect(screen.getByRole('heading', { name: 'StudyMatch' })).toBeTruthy()
  })

  it('redireciona a raiz para a lista de estudantes', async () => {
    renderAt('/')

    expect(await screen.findByRole('heading', { name: 'Estudantes' })).toBeTruthy()
  })

  it('apresenta o estado do sistema em /health', async () => {
    renderAt('/health')

    expect(await screen.findByText('Aplicação: UP')).toBeTruthy()
  })

  it('apresenta o formulário de criação em /students/new', () => {
    renderAt('/students/new')

    expect(screen.getByRole('heading', { name: 'Novo estudante' })).toBeTruthy()
  })
})
