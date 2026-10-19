import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import SelfAssessmentSection from './SelfAssessmentSection'
import { ApiError, saveSelfAssessment } from '../api/students'

// mantém o erro da api verdadeiro e troca só a função que fala com o backend
vi.mock('../api/students', async (importOriginal) => ({
  ...(await importOriginal()),
  saveSelfAssessment: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

// escolhe uma nota a partir da label da competência
function choose(label, value) {
  fireEvent.change(screen.getByLabelText(label), { target: { value } })
}

function chooseAll() {
  choose('Programação', '3')
  choose('Matemática', '4')
  choose('Bases de dados', '2')
  choose('Engenharia de software', '5')
}

function saveButton() {
  return screen.getByRole('button', { name: 'Guardar autoavaliação' })
}

describe('SelfAssessmentSection', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    render(<SelfAssessmentSection studentId={4} />)
  })

  it('mostra um seletor de 1 a 5 por competência, todos vazios', () => {
    for (const label of ['Programação', 'Matemática', 'Bases de dados', 'Engenharia de software']) {
      const select = screen.getByLabelText(label)
      expect(select.value).toBe('')
      // opção vazia + 1..5
      expect(select.querySelectorAll('option')).toHaveLength(6)
    }
  })

  it('só deixa guardar com as quatro competências escolhidas', () => {
    expect(saveButton().disabled).toBe(true)

    choose('Programação', '3')
    choose('Matemática', '4')
    choose('Bases de dados', '2')
    expect(saveButton().disabled).toBe(true)

    choose('Engenharia de software', '5')
    expect(saveButton().disabled).toBe(false)
  })

  it('envia as notas como números e avisa que guardou', async () => {
    saveSelfAssessment.mockResolvedValue({})

    chooseAll()
    fireEvent.click(saveButton())

    expect((await screen.findByRole('status')).textContent).toBe('Autoavaliação guardada.')
    expect(saveSelfAssessment).toHaveBeenCalledWith(4, {
      programming: 3,
      mathematics: 4,
      databases: 2,
      softwareEngineering: 5,
    })
  })

  it('avisa o perfil para recarregar depois de guardar', async () => {
    saveSelfAssessment.mockResolvedValue({})
    const listener = vi.fn()
    window.addEventListener('student-profile-refresh', listener)

    chooseAll()
    fireEvent.click(saveButton())

    await screen.findByRole('status')
    window.removeEventListener('student-profile-refresh', listener)
    expect(listener).toHaveBeenCalledTimes(1)
    expect(listener.mock.calls[0][0].detail).toEqual({ studentId: 4 })
  })

  it('não avisa o perfil quando guardar falha', async () => {
    saveSelfAssessment.mockRejectedValue(new ApiError(0, 'Não foi possível contactar o servidor.'))
    const listener = vi.fn()
    window.addEventListener('student-profile-refresh', listener)

    chooseAll()
    fireEvent.click(saveButton())

    await screen.findByRole('alert')
    window.removeEventListener('student-profile-refresh', listener)
    expect(listener).not.toHaveBeenCalled()
  })

  it('mostra o erro e mantém as escolhas quando guardar falha', async () => {
    saveSelfAssessment.mockRejectedValue(new ApiError(0, 'Não foi possível contactar o servidor.'))

    chooseAll()
    fireEvent.click(saveButton())

    expect((await screen.findByRole('alert')).textContent).toBe(
      'Não foi possível contactar o servidor.'
    )
    expect(screen.getByLabelText('Programação').value).toBe('3')
  })
})
