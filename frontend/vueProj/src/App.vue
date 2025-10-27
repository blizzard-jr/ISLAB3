<script setup>
import { ref } from 'vue'
import MainTable from './MainTable.vue'
import SpecialOperations from './SpecialOperations.vue'
import ToastContainer from './components/ToastContainer.vue'

const currentTab = ref('table')
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <ToastContainer />
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex space-x-8">
              <button
                @click="currentTab = 'table'"
                :class="[
                  'inline-flex items-center px-4 py-2 border-b-2 text-sm font-medium transition-colors',
                  currentTab === 'table'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                Таблица объектов
              </button>
              <button
                @click="currentTab = 'operations'"
                :class="[
                  'inline-flex items-center px-4 py-2 border-b-2 text-sm font-medium transition-colors',
                  currentTab === 'operations'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                Специальные операции
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <MainTable v-if="currentTab === 'table'" />
    <SpecialOperations v-if="currentTab === 'operations'" />
  </div>
</template>

<style scoped></style>
