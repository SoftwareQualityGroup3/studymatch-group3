import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { Link, MemoryRouter, Route, Routes } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import StudentEditPage from './StudentEditPage'
import { ApiError, getStudent, updateStudent } from '../api/students'

vi.mock('../api/students', async (importOriginal) => ({
  ...(await importOriginal()),
  getStudent: vi.fn(),
  updateStudent: vi.fn(),
}))

afterEach(() => {
  cleanup()
})

const ANA = {
  id: 1,
  number: '100',
  name: 'Ana Silva',
  email: 'ana@isep.ipp.pt',
  course: 'LEI',
  curricularYear: 2,
}

const BRUNO = {
  id: 2,
  number: '200',
  name: 'Bruno Costa',
  email: null,
  course: null,
  curricularYear: 3,
}

// o link permite mudar de um estudante para outro sem sair da página de edição
// é o cenário em que o react reaproveita o mesmo componente
function renderAt(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Link to="/students/2/edit">ir para o estudante 2</Link>
      <Routes>
        <Route path="/students/:id/edit" element={<StudentEditPage />} />
        <Route path="/students/:id" element={<p>detalhe aberto</p>} />
      </Routes>
    </MemoryRouter>
  )
}

describe('StudentEditPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    getStudent.mockImplementation(async (id) => (String(id) === '1' ? ANA : BRUNO))
  })

  it('preenche o formulário com os dados do estudante', async () => {
    renderAt('/students/1/edit')

    expect((await screen.findByLabelText('Nome')).value).toBe('Ana Silva')
    expect(screen.getByLabelText('Número de estudante').value).toBe('100')
    expect(screen.getByLabelText('Contacto institucional (email)').value).toBe('ana@isep.ipp.pt')
    expect(screen.getByLabelText('Curso').value).toBe('LEI')
    expect(screen.getByLabelText('Ano curricular').value).toBe('2')
  })

  it('mostra as caixas vazias quando o email e o curso vêm a null', async () => {
    renderAt('/students/2/edit')

    expect((await screen.findByLabelText('Nome')).value).toBe('Bruno Costa')
    expect(screen.getByLabelText('Contacto institucional (email)').value).toBe('')
    expect(screen.getByLabelText('Curso').value).toBe('')
  })

  it('guarda as alterações e volta ao detalhe', async () => {
    updateStudent.mockResolvedValue({ ...ANA, name: 'Ana Maria' })
    renderAt('/students/1/edit')

    fireEvent.change(await screen.findByLabelText('Nome'), { target: { value: 'Ana Maria' } })
    fireEvent.click(screen.getByRole('button', { name: 'Guardar alterações' }))

    expect(await screen.findByText('detalhe aberto')).toBeTruthy()
    expect(updateStudent).toHaveBeenCalledWith('1', {
      number: '100',
      name: 'Ana Maria',
      email: 'ana@isep.ipp.pt',
      course: 'LEI',
      curricularYear: 2,
    })
  })

  it('permite corrigir o número e mostra o erro se já existir (409)', async () => {
    updateStudent.mockRejectedValue(
      new ApiError(409, 'Já existe um estudante com este número.', {
        number: 'Student number 200 already exists',
      })
    )
    renderAt('/students/1/edit')

    fireEvent.change(await screen.findByLabelText('Número de estudante'), {
      target: { value: '200' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Guardar alterações' }))

    expect(await screen.findByText('Student number 200 already exists')).toBeTruthy()
    expect(screen.queryByText('detalhe aberto')).toBeNull()
  })

  it('mostra os dados do estudante 2 depois de mudar do estudante 1', async () => {
    updateStudent.mockResolvedValue(BRUNO)
    renderAt('/students/1/edit')
    expect((await screen.findByLabelText('Nome')).value).toBe('Ana Silva')

    fireEvent.click(screen.getByRole('link', { name: 'ir para o estudante 2' }))

    // o formulário tem de ser recriado, senão ficava a ana nas caixas
    await waitFor(() => expect(screen.getByLabelText('Nome').value).toBe('Bruno Costa'))
    expect(screen.getByLabelText('Número de estudante').value).toBe('200')

    // e o envio tem de ir para o id 2 com os dados do bruno
    fireEvent.click(screen.getByRole('button', { name: 'Guardar alterações' }))
    await waitFor(() => expect(updateStudent).toHaveBeenCalled())
    expect(updateStudent).toHaveBeenCalledWith(
      '2',
      expect.objectContaining({ number: '200', name: 'Bruno Costa' })
    )
  })

  it('esconde o formulário enquanto os dados do estudante novo carregam', async () => {
    renderAt('/students/1/edit')
    await screen.findByLabelText('Nome')

    // a partir daqui o pedido do estudante 2 só termina quando nós quisermos
    let resolveBruno
    getStudent.mockImplementation(
      () => new Promise((resolve) => (resolveBruno = resolve))
    )
    fireEvent.click(screen.getByRole('link', { name: 'ir para o estudante 2' }))

    // nada de formulário com os dados da ana, nem botão para os enviar
    expect(await screen.findByText('A carregar...')).toBeTruthy()
    expect(screen.queryByLabelText('Nome')).toBeNull()
    expect(screen.queryByRole('button', { name: 'Guardar alterações' })).toBeNull()

    resolveBruno(BRUNO)
    expect((await screen.findByLabelText('Nome')).value).toBe('Bruno Costa')
  })

  it('ignora a resposta atrasada do estudante anterior', async () => {
    // o estudante 1 demora e o estudante 2 responde logo
    let resolveAna
    getStudent.mockImplementation((id) =>
      String(id) === '1' ? new Promise((resolve) => (resolveAna = resolve)) : Promise.resolve(BRUNO)
    )
    renderAt('/students/1/edit')

    fireEvent.click(screen.getByRole('link', { name: 'ir para o estudante 2' }))
    expect((await screen.findByLabelText('Nome')).value).toBe('Bruno Costa')

    // a resposta da ana chega tarde e não pode mexer no ecrã
    resolveAna(ANA)
    await Promise.resolve()
    expect(screen.getByLabelText('Nome').value).toBe('Bruno Costa')
  })

  it('diz que o estudante não foi encontrado num 404', async () => {
    getStudent.mockRejectedValue(new ApiError(404, 'Estudante não encontrado.'))

    renderAt('/students/99/edit')

    expect((await screen.findByRole('alert')).textContent).toBe('Estudante não encontrado.')
    expect(screen.queryByLabelText('Nome')).toBeNull()
  })

  it('pede para verificar o backend quando o servidor está desligado', async () => {
    getStudent.mockRejectedValue(new ApiError(0, 'Não foi possível contactar o servidor.'))

    renderAt('/students/1/edit')

    expect((await screen.findByRole('alert')).textContent).toContain('Verifique se o backend')
  })
})
