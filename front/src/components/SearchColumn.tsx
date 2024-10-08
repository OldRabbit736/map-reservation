import SearchBar from "@/components/SearchBar";
import SearchResultList from "@/components/SearchResultList";

function SearchColumn() {
    return (
        <div className="
        w-96 z-20
        grid grid-cols-1 grid-rows-[auto_1fr]
        bg-white"
        >
            <SearchBar/>
            <SearchResultList/>
        </div>
    )
}

export default SearchColumn
