import { useNavigate } from 'react-router-dom'
import { createStudent } from '../api/students'
import StudentForm from '../components/StudentForm'

function StudentCreatePage() {
  // serve para mudar de página por código
  const navigate = useNavigate()

  // o formulário chama isto quando se carrega no botão
  // recebe os dados já preparados
  async function handleSubmit(data) {
    // manda para o servidor e espera
    // se o servidor der erro pára aqui e o erro volta para o formulário
    const created = await createStudent(data)

    // só chega aqui se correu bem e vai para o detalhe do estudante novo
    navigate(`/students/${created.id}`)
  }

  return (
    <section>
      <h2>Novo estudante</h2>

      {/* damos ao formulário a função que cria o estudante */}
      <StudentForm submitLabel="Criar estudante" onSubmit={handleSubmit} />
    </section>
  )
}

export default StudentCreatePage