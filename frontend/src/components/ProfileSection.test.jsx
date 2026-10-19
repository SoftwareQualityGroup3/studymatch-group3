import { act, cleanup, render, screen, waitFor, within } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import ProfileSection from './ProfileSection'
import { ApiError, getStudentProfile } from '../api/students'

// mantém o erro da api verdadeiro e troca só a função que fala com o backend
vi.mock('../api/students', async (importOriginal) => ({
  ...(await importOriginal()),
  getStudentProfile: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

// perfil com as quatro competências, como o backend o devolve
// só a programação tem notas, as outras ainda não têm dados
const PROFILE = {
  profiles: {
    PROGRAMMING: { score: 15.38, evidenceCount: 2, source: 'MIXED' },
    MATHEMATICS: { score: 12, evidenceCount: 0, source: 'SELF_ASSESSMENT' },
    DATABASES: { score: null, evidenceCount: 0, source: 'ACADEMIC' },
    SOFTWARE_ENGINEERING: { score: null, evidenceCount: 0, source: 'ACADEMIC' },
  },
}

// devolve as células da linha de uma competência
function rowOf(label) {
  const row = screen.getByRole('row', { name: new RegExp(label) })
  return within(row).getAllByRole('cell').map((cell) => cell.textContent)
}

// simula o aviso que o S2-06 e o S2-11 enviam depois de guardar
function dispatchRefresh(studentId) {
  act(() => {
    window.dispatchEvent(new CustomEvent('student-profile-refresh', { detail: { studentId } }))
  })
}

describe('ProfileSection', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('mostra a mensagem de carregamento enquanto espera pelo perfil', () => {
    getStudentProfile.mockReturnValue(new Promise(() => {}))

    render(<ProfileSection studentId={4} />)

    expect(screen.getByText('A carregar...')).toBeTruthy()
  })

  it('mostra por competência a pontuação, as evidências e a origem', async () => {
    getStudentProfile.mockResolvedValue(PROFILE)

    render(<ProfileSection studentId={4} />)

    await screen.findByRole('table')
    expect(getStudentProfile).toHaveBeenCalledWith(4)
    expect(rowOf('Programação')).toEqual(['Programação', '15.38 / 20', '2', 'Notas e autoavaliação'])
    expect(rowOf('Matemática')).toEqual(['Matemática', '12.00 / 20', '0', 'Autoavaliação'])
  })

  it('mostra "Sem dados" quando a pontuação é null e nunca 0', async () => {
    getStudentProfile.mockResolvedValue(PROFILE)

    render(<ProfileSection studentId={4} />)

    await screen.findByRole('table')
    expect(rowOf('Bases de dados')).toEqual(['Bases de dados', 'Sem dados', '0', '—'])
    expect(rowOf('Engenharia de software')[1]).toBe('Sem dados')
  })

  it('mostra o 0 quando a pontuação é mesmo 0 (limite da escala)', async () => {
    getStudentProfile.mockResolvedValue({
      profiles: { PROGRAMMING: { score: 0, evidenceCount: 1, source: 'ACADEMIC' } },
    })

    render(<ProfileSection studentId={4} />)

    await screen.findByRole('table')
    expect(rowOf('Programação')).toEqual(['Programação', '0.00 / 20', '1', 'Notas'])
  })

  it('mostra as quatro competências mesmo que o backend não mande alguma', async () => {
    getStudentProfile.mockResolvedValue({ profiles: {} })

    render(<ProfileSection studentId={4} />)

    await screen.findByRole('table')
    // cabeçalho + quatro competências
    expect(screen.getAllByRole('row')).toHaveLength(5)
    expect(rowOf('Programação')[1]).toBe('Sem dados')
  })

  it('mostra o erro quando o perfil não carrega', async () => {
    getStudentProfile.mockRejectedValue(new ApiError(0, 'Não foi possível contactar o servidor.'))

    render(<ProfileSection studentId={4} />)

    expect((await screen.findByRole('alert')).textContent).toContain('Verifique se o backend')
    expect(screen.queryByRole('table')).toBeNull()
  })

  it('recarrega o perfil quando o aviso é do mesmo estudante', async () => {
    getStudentProfile
      .mockResolvedValueOnce({ profiles: {} })
      .mockResolvedValueOnce(PROFILE)

    render(<ProfileSection studentId={4} />)

    await screen.findByRole('table')
    expect(rowOf('Programação')[1]).toBe('Sem dados')

    dispatchRefresh(4)

    await waitFor(() => expect(rowOf('Programação')[1]).toBe('15.38 / 20'))
    expect(getStudentProfile).toHaveBeenCalledTimes(2)
  })

  it('ignora o aviso de outro estudante', async () => {
    getStudentProfile.mockResolvedValue(PROFILE)

    render(<ProfileSection studentId={4} />)

    await screen.findByRole('table')
    dispatchRefresh(9)

    expect(getStudentProfile).toHaveBeenCalledTimes(1)
  })
})
