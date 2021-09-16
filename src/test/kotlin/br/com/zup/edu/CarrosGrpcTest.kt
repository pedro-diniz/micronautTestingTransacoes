package br.com.zup.edu
import br.com.zup.edu.carros.Carro
import br.com.zup.edu.carros.CarroRepository
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@MicronautTest(
    rollback = false, // o rollback é habilitado por padrão no Micronaut. definí-lo como false garante os commits
    transactionMode = TransactionMode.SINGLE_TRANSACTION, // faz o @BeforeEach ser executado na mesma transação do @Test
    transactional = false // torna o auto commit padrão para todas as transações. Testes ficam auto-contidos.
)
class CarrosGrpcTest {

    /**
     * analogia com louças:
     *
     * sujou, limpou (sempre limpa) -> @AfterEach
     * limpou, usou (sempre suja) -> @BeforeEach
     * usa descartável (nunca é sujo) -> rollback = true -> não interessante caso haja triggers, ou com Hibernate
     * usou, jogou fora -> recriar o banco a cada teste, usando o H2
     */

    @Inject
    lateinit var repository: CarroRepository

    @BeforeEach // commitado de acordo com o TransactionMode. Por padrão, é commitado separadamente do @Test.
                    // com o TransactionMode.SINGLE_TRANSACTION, é commitado na mesma transação que o @Test.
    internal fun setUp() {
        repository.deleteAll()
    }

    @AfterEach // commitado em uma transação própria de forma independente
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun deveInserirUmNovoCarro() {

        // cenário executado pelo @BeforeEach

        // ação
        repository.save(Carro(modelo = "Gol", placa = "HPX1234"))

        // validação
        Assertions.assertEquals(1, repository.count())
    }

    @Test
    internal fun deveEncontrarCarroPorPlaca() {

        // cenário executado pelo @BeforeEach
        repository.save(Carro(modelo = "Palio", placa = "OIP9876"))

        // ação
        val encontrado = repository.existsByPlaca("OIP9876")

        // validação
        Assertions.assertTrue(encontrado)


    }
}
