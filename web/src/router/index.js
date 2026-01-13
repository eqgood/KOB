import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView.vue'
import RecordIndexView from '../views/record/RecordIndexView.vue'
import UserBotsIndexView from '../views/user/bots/UserBotsIndexView.vue'
import RanklistIndexView from '../views/ranklist/RanklistIndexView.vue'
import NotFoundView from '../views/error/NotfoundView.vue'


const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/"
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView
  },
  {
    path: "/user/bots/",
    name: "user_bots_index",
    component: UserBotsIndexView
  },
  {
    path: "/404/",
    name: "404",
    component: NotFoundView
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
