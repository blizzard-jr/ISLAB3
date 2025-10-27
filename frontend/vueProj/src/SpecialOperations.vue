<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-5xl mx-auto">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h1 class="text-3xl font-bold text-gray-900 mb-6">Специальные операции</h1>

        <div class="space-y-6">
          <!-- Операция 1: Количество объектов с defenseLevel > X -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Подсчет объектов с уровнем защиты больше заданного
            </h3>
            <div class="flex gap-3">
              <input
                v-model.number="defenseThreshold"
                type="number"
                step="0.1"
                placeholder="Введите уровень защиты"
                class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <button
                @click="countByDefense"
                class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Подсчитать
              </button>
            </div>
            <div v-if="defenseCountResult !== null" class="mt-3 p-3 bg-green-50 rounded-lg">
              <p class="text-green-800">
                Найдено объектов: <span class="font-bold">{{ defenseCountResult }}</span>
              </p>
            </div>
          </div>

          <!-- Операция 2: Объекты с именем, начинающимся на подстроку -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Поиск объектов по началу имени
            </h3>
            <div class="flex gap-3">
              <input
                v-model="namePrefix"
                type="text"
                placeholder="Введите начало имени"
                class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <button
                @click="findByNamePrefix"
                class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Найти
              </button>
            </div>
            <div v-if="namePrefixResults.length > 0" class="mt-3 space-y-2">
              <p class="text-sm text-gray-600">Найдено: {{ namePrefixResults.length }}</p>
              <div class="max-h-48 overflow-y-auto space-y-2">
                <div
                  v-for="creature in namePrefixResults"
                  :key="creature.id"
                  class="p-3 bg-blue-50 rounded-lg"
                >
                  <p class="font-medium">{{ creature.name }}</p>
                  <p class="text-sm text-gray-600">ID: {{ creature.id }}, Тип: {{ creature.creatureType }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Операция 3: Объекты с defenseLevel < X -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Поиск объектов с уровнем защиты меньше заданного
            </h3>
            <div class="flex gap-3">
              <input
                v-model.number="defenseLowerThreshold"
                type="number"
                step="0.1"
                placeholder="Введите уровень защиты"
                class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <button
                @click="findByDefenseLower"
                class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Найти
              </button>
            </div>
            <div v-if="defenseLowerResults.length > 0" class="mt-3 space-y-2">
              <p class="text-sm text-gray-600">Найдено: {{ defenseLowerResults.length }}</p>
              <div class="max-h-48 overflow-y-auto space-y-2">
                <div
                  v-for="creature in defenseLowerResults"
                  :key="creature.id"
                  class="p-3 bg-purple-50 rounded-lg"
                >
                  <p class="font-medium">{{ creature.name }}</p>
                  <p class="text-sm text-gray-600">
                    ID: {{ creature.id }}, Защита: {{ creature.defenseLevel }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Операция 4: Обмен кольцами -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Обмен кольцами между персонажами
            </h3>
            <div class="grid grid-cols-2 gap-3 mb-3">
              <input
                v-model.number="exchangeId1"
                type="number"
                placeholder="ID первого персонажа"
                class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <input
                v-model.number="exchangeId2"
                type="number"
                placeholder="ID второго персонажа"
                class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <button
              @click="exchangeRings"
              class="w-full px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition-colors"
            >
              Обменять кольца
            </button>
            <div v-if="exchangeMessage" class="mt-3 p-3 rounded-lg" :class="exchangeSuccess ? 'bg-green-50' : 'bg-red-50'">
              <p :class="exchangeSuccess ? 'text-green-800' : 'text-red-800'">
                {{ exchangeMessage }}
              </p>
            </div>
          </div>

          <!-- Операция 5: Переместить хоббитов с кольцами в Мордор -->
          <div class="border border-gray-200 rounded-lg p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">
              Переместить всех хоббитов с кольцами в Мордор
            </h3>
            <p class="text-sm text-gray-600 mb-3">
              Эта операция переместит всех хоббитов, у которых есть кольцо, в локацию "Мордор"
            </p>
            <button
              @click="moveHobbitsToMordor"
              class="w-full px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            >
              Переместить в Мордор
            </button>
            <div v-if="mordorMessage" class="mt-3 p-3 rounded-lg" :class="mordorSuccess ? 'bg-green-50' : 'bg-red-50'">
              <p :class="mordorSuccess ? 'text-green-800' : 'text-red-800'">
                {{ mordorMessage }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { api } from './api.js'

// Операция 1: Подсчет объектов с defenseLevel > value
const defenseThreshold = ref<number | null>(null)
const defenseCountResult = ref<number | null>(null)

async function countByDefense() {
  if (defenseThreshold.value === null) {
    alert('Введите уровень защиты')
    return
  }

  try {
    const count = await api.countByDefenseAbove(defenseThreshold.value)
    defenseCountResult.value = count
  } catch (error) {
    alert('Ошибка при выполнении операции: ' + error.message)
    defenseCountResult.value = null
  }
}

// Операция 2: Поиск по началу имени
const namePrefix = ref('')
const namePrefixResults = ref<any[]>([])

async function findByNamePrefix() {
  if (!namePrefix.value) {
    alert('Введите начало имени')
    return
  }

  try {
    namePrefixResults.value = await api.findByNamePrefix(namePrefix.value)
  } catch (error) {
    alert('Ошибка при выполнении операции: ' + error.message)
    namePrefixResults.value = []
  }
}

// Операция 3: Поиск объектов с defenseLevel < value
const defenseLowerThreshold = ref<number | null>(null)
const defenseLowerResults = ref<any[]>([])

async function findByDefenseLower() {
  if (defenseLowerThreshold.value === null) {
    alert('Введите уровень защиты')
    return
  }

  try {
    defenseLowerResults.value = await api.findByDefenseBelow(defenseLowerThreshold.value)
  } catch (error) {
    alert('Ошибка при выполнении операции: ' + error.message)
    defenseLowerResults.value = []
  }
}

// Операция 4: Обмен кольцами
const exchangeId1 = ref<number | null>(null)
const exchangeId2 = ref<number | null>(null)
const exchangeMessage = ref('')
const exchangeSuccess = ref(false)

async function exchangeRings() {
  if (!exchangeId1.value || !exchangeId2.value) {
    alert('Введите ID обоих персонажей')
    return
  }

  if (exchangeId1.value === exchangeId2.value) {
    alert('ID персонажей должны быть разными')
    return
  }

  try {
    const message = await api.swapRings(exchangeId1.value, exchangeId2.value)
    exchangeSuccess.value = true
    exchangeMessage.value = message
  } catch (error) {
    exchangeSuccess.value = false
    exchangeMessage.value = 'Ошибка: ' + error.message
  }
}

// Операция 5: Переместить хоббитов в Мордор
const mordorMessage = ref('')
const mordorSuccess = ref(false)

async function moveHobbitsToMordor() {
  if (!confirm('Вы уверены, что хотите переместить всех хоббитов с кольцами в Мордор?')) {
    return
  }

  try {
    const message = await api.moveHobbitsToMordor()
    mordorSuccess.value = true
    mordorMessage.value = message
  } catch (error) {
    mordorSuccess.value = false
    mordorMessage.value = 'Ошибка: ' + error.message
  }
}
</script>
