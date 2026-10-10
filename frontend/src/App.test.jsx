import { cleanup, render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'
import { getHealth } from './api/health'

vi.mock('./api/health', () => ({
  getHealth: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    getHealth.mockResolvedValue({
      status: 'UP',
      database: 'UP',
    })
  })

  it('apresenta o nome StudyMatch', () => {
    render(<App />)

    expect(
      screen.getByRole('heading', { name: /studymatch/i })
    ).toBeTruthy()
  })

  it('apresenta o estado da aplicação e da base de dados', async () => {
    render(<App />)

    expect(
      screen.getByText('A carregar...')
    ).toBeTruthy()

    expect(
      await screen.findByText('Aplicação: UP')
    ).toBeTruthy()

    expect(
      screen.getByText('Base de dados: UP')
    ).toBeTruthy()
  })

  it('apresenta uma mensagem de erro quando o backend está indisponível', async () => {
    getHealth.mockRejectedValue(
      new Error('Backend indisponível')
    )

    render(<App />)

    expect(
      await screen.findByText(
        'Não foi possível obter o estado da aplicação.'
      )
    ).toBeTruthy()
  })
})
