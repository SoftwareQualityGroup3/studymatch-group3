import { cleanup, render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import HealthPage from './HealthPage'
import { getHealth } from '../api/health'

// troca o pedido ao backend por uma versão falsa que controlamos
vi.mock('../api/health', () => ({
  getHealth: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

describe('HealthPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    // por defeito o backend responde que está tudo bem
    getHealth.mockResolvedValue({
      status: 'UP',
      database: 'UP',
    })
  })

  it('apresenta o estado da aplicação e da base de dados', async () => {
    render(<HealthPage />)

    expect(screen.getByText('A carregar...')).toBeTruthy()
    expect(await screen.findByText('Aplicação: UP')).toBeTruthy()
    expect(screen.getByText('Base de dados: UP')).toBeTruthy()
  })

  it('apresenta uma mensagem de erro quando o backend está indisponível', async () => {
    getHealth.mockRejectedValue(new Error('Backend indisponível'))

    render(<HealthPage />)

    expect(
      await screen.findByText('Não foi possível obter o estado da aplicação.')
    ).toBeTruthy()
  })
})
