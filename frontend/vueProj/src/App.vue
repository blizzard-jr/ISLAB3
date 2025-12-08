<script setup>
import { ref, computed } from 'vue'
import { LogOut, User } from 'lucide-vue-next'
import MainTable from './MainTable.vue'
import SpecialOperations from './SpecialOperations.vue'
import ImportPage from './ImportPage.vue'
import AuthPage from './AuthPage.vue'
import ToastContainer from './components/ToastContainer.vue'
import { useAuth } from './composables/useAuth.js'

const { isAuthenticated, username, isAdmin, logout } = useAuth()

const currentTab = ref('table')

const tabs = computed(() => [
  { id: 'table', label: 'Таблица объектов' },
  { id: 'operations', label: 'Специальные операции' },
  { id: 'import', label: 'Импорт данных' }
])

function handleLogout() {
  logout()
  currentTab.value = 'table'
}

function handleAuthenticated() {
  currentTab.value = 'table'
}
</script>

<template>
  <!-- Экран авторизации -->
  <AuthPage v-if="!isAuthenticated" @authenticated="handleAuthenticated" />

  <!-- Основное приложение -->
  <div v-else class="min-h-screen bg-gray-50">
    <ToastContainer />
    
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <!-- Навигация -->
          <div class="flex">
            <div class="flex space-x-8">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="currentTab = tab.id"
                :class="[
                  'inline-flex items-center px-4 py-2 border-b-2 text-sm font-medium transition-colors',
                  currentTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- Информация о пользователе -->
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2 text-sm text-gray-600">
              <User :size="18" />
              <span>{{ username }}</span>
              <span 
                v-if="isAdmin" 
                class="px-2 py-0.5 bg-purple-100 text-purple-700 rounded-full text-xs font-medium"
              >
                Admin
              </span>
            </div>
            <button
              @click="handleLogout"
              class="flex items-center gap-1 px-3 py-1.5 text-sm text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            >
              <LogOut :size="16" />
              Выйти
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Контент -->
    <MainTable v-if="currentTab === 'table'" />
    <SpecialOperations v-if="currentTab === 'operations'" />
    <ImportPage v-if="currentTab === 'import'" />
  </div>
</template>

<style scoped></style>
