object Modules {
    const val App = ":app"

    object Features {
        private const val prefix = ":features"

        object Base {
            private const val prefix1 = ":base"

            const val Core = "$prefix$prefix1:core"
            const val Navigation = "$prefix$prefix1:navigation"
            const val Elements = "$prefix$prefix1:elements"
        }

        const val Home = "$prefix:home"
        const val Schedule = "$prefix:schedule"
        const val Account = "$prefix:account"
        const val Misc = "$prefix:misc"
    }

    object Domain {
        const val Base = ":domain:base"
        const val Schedule = ":domain:schedule"
        const val Account = ":domain:account"
    }

    object Data {
        const val Base = ":data:base"
        const val Schedule = ":data:schedule"
        const val Account = ":data:account"
    }
}