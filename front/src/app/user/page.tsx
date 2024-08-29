'use client'

import useAuth from "@/hooks/useAuth";
import {useRouter} from "next/navigation";
import {logout} from "@/api";

function UserPage() {
    const router = useRouter()
    const {status, user} = useAuth()
    if (status === 'loading') return <div>Loading</div>
    if (status === 'unauthenticated') {
        // 페이지 히스토리 최상단을 user에서 login으로 변경하고 login으로 이동.
        // login 페이지에서 로그인 포기하고 뒤로 가기 누르면 user 페이지가 아니라 전 화면(대표적으로 메인 화면)으로 돌아가기 위해서임
        router.replace('/login')
        return
    }

    return (
        <>
            <Bar user={user}/>
            <Body/>
        </>
    )
}

function Bar({user}: { user: CustomerInfo }) {
    const router = useRouter()

    const handleLogoutClick = async () => {
        try {
            await logout()
            router.push('/')
        } catch (e) {
            console.log(e)
            console.log('error')
        }
    }

    return (
        <div className="absolute top-0 h-14 bg-gray-200 w-full flex justify-between p-3">
            <p className="flex justify-center items-center">{user.username}님 환영합니다.</p>
            <div
                className="flex justify-center items-center bg-white rounded p-3 hover:cursor-pointer"
                onClick={handleLogoutClick}
            >
                로그 아웃
            </div>
        </div>
    )
}

function Body() {
    return (
        <div className="mt-14">hi</div>
    )
}

export default UserPage
