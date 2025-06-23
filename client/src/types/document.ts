import { UUID } from "crypto";

export interface Document {
    id: UUID,
    userId : UUID
    fileName: string,
    status: string
}

