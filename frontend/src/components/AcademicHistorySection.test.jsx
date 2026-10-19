import {
  cleanup,
  fireEvent,
  render,
  screen,
  waitFor,
  within,
} from '@testing-library/react'

import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import AcademicHistorySection from './AcademicHistorySection'

import {
  AcademicRecordApiError,
  createAcademicRecord,
  listAcademicRecords,
  listCourseUnits,
} from '../api/academicRecords'

// Substitui as funções reais da API por versões simuladas.
// Assim os testes não precisam do backend ligado.
vi.mock('../api/academicRecords', async (importOriginal) => ({
  ...(await importOriginal()),
  createAcademicRecord: vi.fn(),
  listAcademicRecords: vi.fn(),
  listCourseUnits: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

describe('AcademicHistorySection', () => {
  beforeEach(() => {
    vi.clearAllMocks()

    // UCs disponíveis no dropdown durante os testes.
    listCourseUnits.mockResolvedValue([
      {
        code: 'ES',
        name: 'Engenharia de Software',
        ects: 6,
      },
      {
        code: 'IA',
        name: 'Inteligência Artificial',
        ects: 6,
      },
    ])
  })

  it('mostra os resultados académicos do estudante', async () => {
    // Simula um histórico com um resultado.
    listAcademicRecords.mockResolvedValue([
      {
        id: 1,
        courseUnitCode: 'ES',
        courseUnitName: 'Engenharia de Software',
        grade: 15,
        academicYear: '2026/27',
        semester: 1,
      },
    ])

    render(
      <AcademicHistorySection studentId={1} />
    )

    expect(
      screen.getByText('A carregar histórico...')
    ).toBeTruthy()

    const table = await screen.findByRole('table')

    expect(
      within(table).getByText('Engenharia de Software')
    ).toBeTruthy()

    expect(screen.getByText('15')).toBeTruthy()
    expect(screen.getByText('2026/27')).toBeTruthy()
  })

  it('mostra mensagem quando não existem resultados', async () => {
    // Simula um estudante sem histórico.
    listAcademicRecords.mockResolvedValue([])

    render(
      <AcademicHistorySection studentId={1} />
    )

    expect(
      await screen.findByText('Sem resultados académicos.')
    ).toBeTruthy()
  })

  it('regista um resultado e atualiza o histórico', async () => {
    // Na primeira chamada o histórico está vazio.
    // Na segunda chamada já aparece o resultado criado.
    listAcademicRecords
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([
        {
          id: 2,
          courseUnitCode: 'IA',
          courseUnitName: 'Inteligência Artificial',
          grade: 17,
          academicYear: '2026/27',
          semester: 2,
        },
      ])

    createAcademicRecord.mockResolvedValue({
      id: 2,
    })

    const profileRefresh = vi.fn()

    window.addEventListener(
      'student-profile-refresh',
      profileRefresh,
      { once: true }
    )

    render(
      <AcademicHistorySection studentId={1} />
    )

    await screen.findByText('Sem resultados académicos.')

    // Preenche o formulário.
    fireEvent.change(
      screen.getByLabelText('Unidade curricular'),
      {
        target: { value: 'IA' },
      }
    )

    fireEvent.change(
      screen.getByLabelText('Nota'),
      {
        target: { value: '17' },
      }
    )

    fireEvent.change(
      screen.getByLabelText('Ano letivo'),
      {
        target: { value: '2026/27' },
      }
    )

    fireEvent.change(
      screen.getByLabelText('Semestre'),
      {
        target: { value: '2' },
      }
    )

    fireEvent.click(
      screen.getByRole('button', {
        name: 'Registar resultado',
      })
    )

    // Confirma que o frontend enviou os dados certos para a API.
    await waitFor(() => {
      expect(createAcademicRecord).toHaveBeenCalledWith(
        1,
        {
          courseUnitCode: 'IA',
          grade: 17,
          academicYear: '2026/27',
          semester: 2,
        }
      )
    })

    // O novo resultado deve aparecer na tabela.
    const table = await screen.findByRole('table')

    expect(
      within(table).getByText('Inteligência Artificial')
    ).toBeTruthy()

    // Confirma que o evento para atualizar o perfil foi lançado.
    expect(profileRefresh).toHaveBeenCalled()
  })

  it('mostra erro quando o resultado já existe', async () => {
    listAcademicRecords.mockResolvedValue([])

    // Simula o erro 409 devolvido pelo backend.
    createAcademicRecord.mockRejectedValue(
      new AcademicRecordApiError(
        409,
        'Student 1 already has a record for course unit ES',
        {
          courseUnitCode:
            'Student 1 already has a record for course unit ES',
        }
      )
    )

    render(
      <AcademicHistorySection studentId={1} />
    )

    await screen.findByText('Sem resultados académicos.')

    fireEvent.change(
      screen.getByLabelText('Unidade curricular'),
      {
        target: { value: 'ES' },
      }
    )

    fireEvent.change(
      screen.getByLabelText('Nota'),
      {
        target: { value: '15' },
      }
    )

    fireEvent.change(
      screen.getByLabelText('Ano letivo'),
      {
        target: { value: '2026/27' },
      }
    )

    fireEvent.click(
      screen.getByRole('button', {
        name: 'Registar resultado',
      })
    )

    const errors = await screen.findAllByText(
      'Student 1 already has a record for course unit ES'
    )

    // Garante que o erro aparece só uma vez.
    expect(errors).toHaveLength(1)
  })

  it('mostra erro quando não consegue carregar o histórico', async () => {
    // Simula uma falha ao pedir o histórico.
    listAcademicRecords.mockRejectedValue(
      new Error('Não foi possível carregar o histórico.')
    )

    render(
      <AcademicHistorySection studentId={1} />
    )

    expect(
      (await screen.findByRole('alert')).textContent
    ).toBe('Não foi possível carregar o histórico.')
  })
})
