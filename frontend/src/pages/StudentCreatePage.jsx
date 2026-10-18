import { useNavigate } from 'react-router-dom'
import { createStudent } from '../api/students'
import StudentForm from '../components/StudentForm'

function StudentCreatePage() {
  // serve para mudar de página por código
  const navigate = useNavigate()

  // o formulário chama isto quando se carrega no botão, já com os dados preparados
  async function handleSubmit(data) {
    // se o backend der erro, o await lança e o erro volta para o formulário
    const created = await createStudent(data)

    // só chega aqui se correu bem, e vai para o detalhe do estudante novo
    navigate(`/students/${created.id}`)
  }

  return (
    <section>
      <h2>Novo estudante</h2>

      {/* sem estudante inicial o formulário começa vazio */}
      <StudentForm submitLabel="Criar estudante" onSubmit={handleSubmit} />
    </section>
  )
}

export default StudentCreatePage
