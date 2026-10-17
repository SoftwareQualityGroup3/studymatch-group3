import { cleanup, render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'
import { listStudents } from './api/students'

vi.mock('./api/students', () => ({
  listStudents: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    listStudents.mockResolvedValue([])
  })

  it('apresenta o nome StudyMatch', () => {
    render(
      <MemoryRouter initialEntries={['/students']}>
        <App />
      </MemoryRouter>
    )

    expect(screen.getByRole('heading', { name: 'StudyMatch' })).toBeTruthy()
  })

  it('redireciona a raiz para a lista de estudantes', async () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>
    )

    expect(await screen.findByRole('heading', { name: 'Estudantes' })).toBeTruthy()
  })
})