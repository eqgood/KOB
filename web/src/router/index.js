import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView.vue'
import RecordIndexView from '../views/record/RecordIndexView.vue'
import RecordContentView from '../views/record/RecordContentView.vue'
import UserBotsIndexView from '../views/user/bots/UserBotsIndexView.vue'
import RanklistIndexView from '../views/ranklist/RanklistIndexView.vue'
import NotFoundView from '../views/error/NotfoundView.vue'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView.vue'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView.vue'
import store from '@/store/index.js'
import UserAccountAcWingWebRecieveCodeView from '@/views/user/account/UserAccountAcWingWebRecieveCodeView.vue'
import UserInfoCenterView from '@/views/user/info/UserInfoCenterView.vue'
import UserAccountForgetPasswordView from '@/views/user/account/ForgetPasswordView.vue'

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/record/:recordId/",
    name: "record_content",
    component: RecordContentView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/user/bots/",
    name: "user_bots_index",
    component: UserBotsIndexView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/user/infocenter/",
    name: "user_info_center",
    component: UserInfoCenterView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/user/account/forget_password/",
    name: "user_account_forget_password",
    component: UserAccountForgetPasswordView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/user/account/acwing/web/receive_code/",
    name: "user_account_acwing_web_receive_code",
    component: UserAccountAcWingWebRecieveCodeView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFoundView,
    meta: {
      requestAuth: false
    }
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

router.beforeEach((to, from, next) => {
  if(to.meta.requestAuth && !store.state.user.is_login){
    next({name: "user_account_login"});
  }else{
    next();
  }
})

export default router
