package io.kached.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import kotlin.reflect.typeOf

@ExperimentalCoroutinesApi
class JacksonSerializerTest {

    private val serializer = JacksonSerializer(ObjectMapper().registerModule(KotlinModule()))

    class Person(val id: String, val name: String) {
        companion object {
            val INSTANCE = Person("1", "Gustavo")
            val JSON = "{\"id\":\"1\",\"name\":\"Gustavo\"}"
            val KCLASS = Person::class
            val KTYPE = typeOf<Person>()
        }
    }

    @Test
    fun serializeWithJacksonSerializer() = runBlockingTest {
        val str = serializer.serialize(Person.INSTANCE, Person.KCLASS, Person.KTYPE)
        Assert.assertEquals(Person.JSON, str)
    }

    @Test
    fun deserializeWithJacksonSerializer() = runBlockingTest {
        val result = serializer.deserialize(Person.JSON, Person.KCLASS, Person.KTYPE)
        Assert.assertEquals(Person.INSTANCE.id, result.id)
        Assert.assertEquals(Person.INSTANCE.name, result.name)
    }
}
